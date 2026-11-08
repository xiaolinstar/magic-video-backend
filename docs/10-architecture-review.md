# 10 · 架构审查与设计优化建议

> 审查时间：2026-10-01
> 审查范围：`gateway` / `auth` / `core` / `multimedia` / `has` / `common` 全量源码 + 编排文件 + DDL
> 方法：静态检索 + 源码阅读 + **3 处运行时实证验证**（MQ 序列化往返、Spring AMQP / MyBatis-Plus 字节码语义核对）
>
> 本文档回答两个问题：**当前架构离"能跑通"还差多远**、**接下来按什么顺序改最划算**。

> ⚠️ **本文是 2026-10-01 的时点快照，不是当前状态。**
> 此后 2026-11-08 已完成 **Phase 0（9 个 P0 全修）+ 路线 B（彻底移除 Nacos）**，
> 本文的 P0-1 ~ P0-9、P1-4、P1-10、P1-11 均已关闭。
> 当前状态请看 [`09-tech-debt.md`](09-tech-debt.md) 的「修复进度跟踪」；
> 决策过程见 [ADR-0006](adr/0006-async-transcoding.md)、[ADR-0007](adr/0007-rabbitmq-consumer-ack.md)、[ADR-0008](adr/0008-remove-nacos.md)。

---

## 结论摘要

整体判断：**服务拆分与技术选型是合理的，主流程的骨架也在，但"最后一公里"没接通。**

拆开看：

| 维度 | 评价 | 说明 |
|---|---|---|
| 服务拆分 | ✅ 合理 | 5 个服务边界清晰，无跨模块直调 Service（唯一跨模块链路走 Dubbo），无"为了微服务而微服务" |
| 技术选型 | ✅ 合理 | Boot 3 / Nacos / Dubbo / MyBatis-Plus / Sa-Token / MinIO / RabbitMQ 组合对路，无过度设计 |
| 接口分层 | ⚠️ 半成品 | 77 个端点直接返回 Entity，Entity 与 DTO 边界只在 `core` 的 3 个 Controller 做了 |
| 错误处理 | ❌ 有系统性问题 | 17 处 `catch(Exception) { log.error(e.getMessage()) }` 把服务端 500 伪装成客户端 400 |
| 数据一致性 | ❌ 有系统性问题 | 全模块 **0 处 `@Transactional`**；乐观锁因调用形态错误而完全失效 |
| 异步链路 | ❌ 实际从未跑通 | 消息类型与可信包白名单冲突（**已实证**），且落库通道两端都断 |
| 配置管理 | ❌ 名不副实 | Nacos 配置中心只在 prod 启用且数据有误；基础设施地址硬编码进 jar |
| 测试 | ❌ 基本为零 | 7 个测试文件全是连真实中间件的集成测试，0 个纯单元测试 |

**一句话**：这不是"架构设计错了"，而是"**架构没被验证过**"。核心链路（上传 → 转码 → 落库 → 播放）
从未端到端跑通过一次，所以链路上的断点一直没人发现。

---

## 一、必须先修：阻断主流程（5 项）

### P0-1 · MQ 消息类型与可信包白名单冲突 —— 转码链路从未跑通 🔴 **已实证**

**位置**
- 生产端 `common/common-message/.../MessageProducerImpl.java:43` — 发送的是 `java.net.URL` 对象
- 转换器 `common/common-message/.../RabbitMQConfig.java:32-46` — 可信包仅 `cn.xiaolin.message.entity`、`cn.xiaolin.message.dto`
- 消费端 `has/.../MessageConsumerImpl.java:29` — 按 `URL` 接收

**实证过程**（用项目真实依赖 spring-amqp 3.1.12 + jackson-databind 2.17.3 跑往返）：

```
发送端 __TypeId__ = java.net.URL
>>> 消费端反序列化失败：
    java.lang.IllegalArgumentException: The class 'java.net.URL' is not in the trusted packages:
    [java.util, java.lang, cn.xiaolin.message.entity, cn.xiaolin.message.dto]
```

**影响**：消费端抛 `MessageConversionException` → AUTO ack 失败 → `defaultRequeue=true` →
**同一条毒消息无限重投，`has` 服务空转**。视频上传后永远不会被转码。

**为什么长期没暴露**：`publish(ResourceMessage)` 走的是白名单内的 `cn.xiaolin.message.entity`，
那条路径是通的。问题只出在实际使用的 `sendVideoMessage(URL)` 上。

**修复方向**（二选一，推荐 A）
- **A（推荐）**：改用 `ResourceMessage` DTO 传输，把 URL 放进字段而不是当消息体类型。DTO 在白名单内，且已有现成类型。
- **B**：把 `java.net.URL` 加入可信包 —— 但 `URL` 内部持有 `InetAddress`，反序列化面偏大，不建议。

> 顺带修：`MessageProducerImpl` 的 `publish(exchange, msg)` / `publish(exchange, routingKey, msg)`
> 两个重载仍是 `NotImplementedException`。

---

### P0-2 · auth 自定义 SQL 硬编码库名 `dev_auth.` —— local/prod 环境登录必挂 🔴

**位置**：`auth/src/main/resources/mapper/SysUserMapper.xml`（9 处）、`SysRoleMapper.xml`（2 处）

```xml
<select id="getUserByUsername" resultType="cn.xiaolin.auth.domain.entity.SysUser">
    select id, username, email, phone, password, admission
    from dev_auth.sys_user          <!-- ← 硬编码库名 -->
    where username = #{username}
</select>
```

**影响**（两个层面）

1. **库名不匹配**：auth 的 local profile 数据源是 `jdbc:mysql://magic-mysql:3306/local_auth`
   （`auth/.../bootstrap-local.yaml:44`），dev 是 `dev_auth`。因此在 **local（最常用的全容器体验模式）
   与 prod 环境，`getUserByUsername`（登录入口）、`listRolesByUserId`（Dubbo 权限下发）
   会因表不存在而直接报错**。这不是概率问题，是**必然复现**。
2. **风格不一致**：`SysPermissionMapper.xml` 用的是裸表名（依赖默认库），另两个 XML 用带库名前缀。

**修复方向**
- 全部去掉 `dev_auth.` 前缀，统一用裸表名（依赖连接的默认库），与 `SysPermissionMapper.xml` 对齐；
- 更彻底的做法：自定义 XML 改用 `${database}` 之类的属性占位，但裸表名已经够用且更简单。

---

### P0-3 · 逻辑删除被自定义 SQL 完全绕过 —— 已注销用户仍可登录 🔴

**位置**：`auth/src/main/resources/mapper/` 下全部 6 个 XML

`@TableLogic` 只对 MyBatis-Plus **自动生成**的 SQL 生效。自定义 XML 不经过该逻辑，
而这批 XML **一个 `deleted = 0` 条件都没有**：

| XML 方法 | 影响 |
|---|---|
| `getUserByUsername` | **已逻辑删除的用户仍能登录** |
| `listPermsByUserId` / `listPermsWithRoleByUserId` | 已删除权限仍通过 Dubbo 下发给网关 |
| `listRolesByUserId` | 已删除角色仍生效 |
| `listUsersByRoleId` / `listUsersByPermId` / `listUsersWithRoleByPermId` | 已删除用户仍出现在查询结果 |

**修复方向**：所有自定义 `<select>` 补 `deleted = 0` 条件（关联表也要补）。

---

### P0-4 · 转码结果的落库通道两端都断 🔴

**位置**：`core/.../DubboResourceService.java:18,23` + `has/.../DubboResourceClient.java`

| 端 | 状态 |
|---|---|
| 提供方 `core` | `getResourceById` / `saveResource` **均抛 `NotImplementedException`** |
| 消费方 `has` | `DubboResourceClient` **全仓无任何调用点** —— 写好了却没接上 |

**影响**：即使 P0-1 修好、转码成功，产物（m3u8/mpd）也**永远不会写进 `video_source` 表**，
`/resource/all`、`/resource/latest` 这些接口拿不到播放源。**整条异步链路是断的。**

**修复方向**（按序）：
1. `core` 实现 `saveResource` → 写 `video_source` 表，`getResourceById` → 读 `video_source`；
2. `has` 在转码成功后调用 `DubboResourceClient.saveResource(...)`；
3. `ResourceDto` 字段与 `video_source` 表结构对齐（需先确认 `m3u8`/`mpd` 字段是否已存在）。

---

### P0-5 · multimedia 的「合并 → 上传 → 发消息」被一个 `if` 包住 🔴

**位置**：`multimedia/.../service/impl/VideoServiceImpl.java:234`

```java
if (!targetFile.exists()) {          // ← 合并、上传、发消息 全在这个 if 里
    // 1. 合并分片
    // 2. 上传视频到 minio
    // 3. 消息生产，并发送到消息队列
    try {
        messageProducer.sendVideoMessage(new URL(videoUrl));
    } catch (MalformedURLException e) {
        log.error("发送视频URL失败：{}", e.getMessage());   // ← 吞掉，消息静默丢失
    }
}
```

**影响**

1. **重复调用 merge 接口 → 整段被跳过 → MQ 消息永不补发**。合并文件已存在（无论上次是否发过消息）
   就直接 return，视频永远不会被转码。
2. 发消息失败被 `catch` 吞掉，**无 outbox、无补偿、无重试、无告警**。

**修复方向**
- 把「合并」「上传」「发消息」拆开：合并文件的"已存在"判断只包住合并步骤；
- 发消息失败要么向上抛（让客户端重试），要么引入本地消息表（outbox）——个人项目建议先选前者。

---

### P0-6 · ffmpeg 同步阻塞，且占用唯一的 MQ 消费线程 🔴

**位置**：`has/.../MediaKitServiceImpl.java:99-135, 263-305` + `has/.../MessageConsumerImpl.java:28-31`

```java
@RabbitListener(queues = {MessageQueueConsts.QUEUE_MEDIA_RESOURCE})   // 无 concurrency、无 ackMode
public void pullUrlMessage(URL url) {
    mediaKitService.media2Dash(url.toString());   // 内部 ProcessBuilder.waitFor()，无超时
}
```

| 问题 | 后果 |
|---|---|
| `waitFor()` 无 timeout | 远程 URL 网络挂起 → 线程永久占用，永不退出 |
| Spring AMQP 默认 concurrency = 1 | **整个 has 服务卡死在一个视频上**，后续任务全部堆积 |
| 转码直接跑在 MQ 消费线程 | 消费线程被长任务占满，等于没有消费能力 |
| `has/.../AppConfig.java:37-43` 声明了 `FFmpeg`/`FFprobe` Bean 但代码不用 | 实际调的是 PATH 上的系统二进制，`net.bramp.ffmpeg` 是死依赖，且**二进制调用无法在单测中 mock** |

**修复方向**（ADR-0006 已起草）
1. `@RabbitListener` 加 `concurrency` + `ackMode = MANUAL`；
2. 转码提交到**独立线程池**执行，消费线程立即 ack（配合幂等键）；
3. `waitFor(timeout, unit)` + `destroyForcibly()`；
4. 恢复 `mediaExecutor` 线程池的接线（见 P1-5）。

---

## 二、稳定性与数据一致性（5 项）

### P1-1 · 全模块零事务 + 17 处吞异常

**事实**：`@Transactional` 在 4 个模块的用量是 **0**。
但 `import org.springframework.transaction.support.TransactionTemplate` 出现在 **15 个 ServiceImpl** 中，
而 `transactionTemplate` **零匹配** —— 纯死 import，说明曾经打算加事务但没落地。

**17 处统一的吞异常模式**：

```java
catch (Exception e) { log.error(e.getMessage()); }   // saved 保持 false
```

位置：`core/AwardServiceImpl.java:110`、`CategoryServiceImpl.java:61`、`DistrictServiceImpl.java:62`、
`TagServiceImpl.java:60`、`VideoActorServiceImpl.java:62`、`VideoCategoryServiceImpl.java:63`、
`VideoDirectorServiceImpl.java:64`、`VideoDistrictServiceImpl.java:62`、`VideoMakerServiceImpl.java:78`、
`VideoScripterServiceImpl.java:62`、`VideoTagServiceImpl.java:63`、
`auth/SysPermissionServiceImpl.java:71`、`SysRoleServiceImpl.java:65`、
`SysRolePermissionServiceImpl.java:59`、`SysUserPermissionServiceImpl.java:62`、
`SysUserRoleServiceImpl.java:61`、`SysUserServiceImpl.java:92`

**双重危害**

1. **错误类型被篡改**：唯一键冲突、连接池耗尽、字段超长 → 全部降级为 `Optional.empty()` →
   Controller 映射成 `Result.badRequest()`（**HTTP 400**）。生产出问题时，第一反应会去查客户端而不是服务端。
2. **堆栈丢失**：`e.getMessage()` 不含 cause 链，定位困难。

**修复方向**
- 业务异常（唯一键冲突等）用明确的自定义异常，只捕获已知类型，不要 `catch (Exception e)`；
- 未知异常**必须重新抛出**，交给 `GlobalExceptionHandler` 统一转 500；
- 日志用 `log.error("上下文描述", e)` 保留堆栈；
- 多表写入（如"新增用户 + 授予角色"）加 `@Transactional(rollbackFor = Exception.class)`。

---

### P1-2 · 乐观锁形同虚设

**位置**：所有更新路径走 `update(updateWrapper)`，例如 `core/AwardServiceImpl.java:78`。

MyBatis-Plus 的 `IService.update(Wrapper<T>)` 内部是 `baseMapper.update(null, updateWrapper)`，
**实体参数为 `null`**。而 `OptimisticLockerInnerInterceptor`（已注册于
`common/common-db/.../MybatisPlusConfig.java:25`）只有在实体非 null 且含 `@Version` 字段时
才会把版本号拼进 WHERE 条件。

**结果**：21 个 entity 都标了 `@Version`，但**并发更新互相覆盖，无任何冲突检测**。
拦截器是注册了的，只是永远不触发。

**修复方向**：更新时传入带版本号的实体（`updateById(entity)` 语义），或显式在 wrapper 里
`setSql("revision = revision + 1")` + `eq(Award::getRevision, dto.getRevision())`。

> 注：`@TableLogic` 配合 `removeById(id)` 是**正确**的（MP 自动追加 `deleted = 0`），
> 问题只出在自定义 XML（P0-3）与乐观锁。

---

### P1-3 · 缓存没有失效策略

**位置**：`core/.../VideoServiceImpl.java:47` 有 `@Cacheable(value = "video", key = "#id")`，
全仓 **0 处 `@CacheEvict` / `@CachePut`**。

**影响**：视频更新或删除后，最长 10 分钟（TTL）仍返回旧数据。

**修复方向**：在 `VideoServiceImpl` 的更新/删除方法上加 `@CacheEvict(value = "video", key = "#id")`；
若后续有多实例，注意 `@CacheEvict` 只清本地/当前实例的 key，需配合 Redis pub/sub 或改用 TTL 更短。

---

### P1-4 · RabbitMQ 消费者配置缺失

**现状**：`@RabbitListener(queues = {QUEUE_MEDIA_RESOURCE})` —— 没有 `concurrency`、没有 `ackMode`、
没有 `prefetch`、没有 `containerFactory`；4 个模块的 yaml 中**无任何 `spring.rabbitmq.listener.*`**。

| 缺失 | 后果 |
|---|---|
| 无 ackMode | 用 Spring AMQP 默认 AUTO ack，转码失败时消息去向不确定 |
| 无 DLQ | `RabbitMQConfig.java:62-65` 的 `new Queue(...)` 未设 `x-dead-letter-exchange` → 失败消息被无限 requeue |
| 无幂等 | 重复投递会重复转码并覆盖上传 MinIO（`MediaKitServiceImpl.java:249-258` 目录已存在则复用旧目录） |
| 无 publisher confirm | `convertAndSend` 无 confirm/return 回调，消息是否落到 broker 不可知 |

详见 ADR-0007。

---

### P1-5 · 磁盘不清理 + 定时任务是死代码

| 位置 | 现象 |
|---|---|
| `multimedia/.../VideoServiceImpl.java:171-200` | 分片写入 `videoFileDir/<md5>/`，**上传后不删除** |
| `multimedia/.../VideoServiceImpl.java:232-266` | 合并文件上传 MinIO 后**不删除** |
| `multimedia/.../VideoServiceImpl.java:204-207` | `abortUpload` 直接 `return false`，中断上传不做任何清理 |
| `has/.../MediaKitServiceImpl.java:83-94, 247-258` | `~/hls-video/<md5>/`、`~/dash-video/<md5>/` 转码产物**从不删除**，随视频数线性增长 |
| `multimedia/.../config/ScheduleTask.java:18` | `@EnableScheduling` 开了，但 `videoResourceScan()` **没有 `@Scheduled` 注解**，只有一句 `System.out.println` → **定时任务从未运行** |
| `multimedia/.../config/ThreadPoolConfig.java:31` | `mediaExecutor` 线程池**只在定义处 + `CleanupServiceImpl.shutdown()` 出现**，从未提交任务 → 死代码 |

**修复方向**
- 转码完成后立即删除临时目录；或加 `@Scheduled(cron = ...)` 清理 7 天前的目录（**记得真正加 `@Scheduled`**）；
- `abortUpload` 补上分片目录删除；
- 把 `mediaExecutor` 真正用起来（P0-6 的转码异步化正好需要它）。

---

### P1-6 · DASH 转码参数自相矛盾

**位置**：`has/.../MediaKitServiceImpl.java:267-272`

`-map 0:v:0 -map 0:a:0` 重复 3 次，但配了 3 组 `-b:v` / `-s:v`。
**实际只输出 1 路，第 2、3 组码率参数静默失效** —— 转码质量与预期不符，且不会报错。

另外 ffmpeg 的 stderr 被全量打到 INFO（`:117-119`、`:286-288`），大文件转码会产生海量日志。

---

## 三、安全（4 项）

### P2-1 · MinIO bucket 公共可读

**位置**：`multimedia/config/AppConfig.java:66-72`

```java
// 设置存储桶策略为公共可读
String policy = "{...\"Action\":[\"s3:GetObject\"]...\"Principal\":{\"AWS\":[\"*\"]}...}";
```

**影响**：任何人拿到 URL 就能下载原片。点播场景下原片通常不该公开。

**修复方向**：bucket 改私有，后端通过**预签名 URL**（短有效期）下发播放地址；
或至少按 bucket 区分（缩略图公开、原片私有）。

### P2-2 · 登录接口无失败限速

**位置**：`auth/.../service/impl/SysUserServiceImpl.java:122-130`

可无限暴力破解。**修复方向**：Redis 计数，按「用户名 + IP」5 次失败锁 5 分钟。

### P2-3 · 敏感配置默认值明文写在 yaml 里

`bootstrap-*.yaml` 里 `password: ${MYSQL_PASSWORD:123456}` 形式虽然支持环境变量覆盖，
但**默认值明文入库**（MySQL `123456`、RabbitMQ `123456rabbit`、MinIO 账号密钥）。
仓库已开启 `secret_scanning`，历史 `.env.local` 中仅有占位符、无真实密钥 —— 这一点是干净的。

**修复方向**：生产 profile 不提供默认值（`${MYSQL_PASSWORD}`，缺失即启动失败），强制显式注入。

### P2-4 · `/auth/kick-out` 的权限校验不足

`kick-out` 已从免登录白名单移除（ADR-0004），但**网关侧只校验了「拥有 common 角色」**，
没有校验「被踢者是否在自己管理范围内」。角色模型能支撑更细的粒度时值得补。

---

## 四、架构性问题（设计层面，8 项）

这一节是本次审查的**核心产出** —— 上面 P0/P1 是"修 bug"，这里是"改设计"。

### A-1 · Nacos 配置中心形同虚设，配置数据还写错了 🔴

**事实**

1. `local` / `dev` profile 全部 `spring.cloud.nacos.config.enabled: false`，
   **只有 `prod` 启用** → 日常开发完全用不到配置中心；
2. prod 用的配置数据在 `static/nacos/DEFAULT_GROUP/*.yaml`，**需要人工导入 Nacos 控制台**，
   仓库里没有任何导入脚本或文档说明；
3. **配置文件本身是错的**：

   | 文件 | 问题 |
   |---|---|
   | `static/nacos/DEFAULT_GROUP/core-dev.yaml` | `spring.application.name: auth`（**应为 core**，复制粘贴没改） |
   | `static/nacos/DEFAULT_GROUP/core-dev.yaml` | 指向 `magic-dev-mysql` / `magic-dev-redis` —— 这两个容器名在 `docker-compose-dev.yaml` 里**不存在**（实际是 `magic-mysql` / `magic-redis`） |
   | `static/nacos/DEFAULT_GROUP/gateway-dev.yaml` | Redis 写 `localhost`，而 core 写 `magic-dev-redis` —— **两处不一致** |
   | `has` 服务 | **完全没有对应的 Nacos 配置文件** |

4. 同模块的 `bootstrap-dev.yaml` 里 `spring.cloud.nacos.config.enabled: false` 位于 `config:` 段下，
   而 `discovery:` 段没有 —— 结构上是对的，但**没人往 Nacos 里推过配置**，所以这些 `false` 一直是生效的。

**结论**：项目名义上是"Nacos 注册中心 + 配置中心"，**实际上只有注册中心在用**，
配置全部硬编码在打包进 jar 的 yaml 里。

**优化建议**

选一条路，别停在中间态：

- **路线 A（推荐，符合"一步步迁移"）**：**承认当前没有配置中心**，把 `static/nacos/` 目录删掉或标注为"待启用"，
  在 README 里写清"配置当前在 jar 内"。→ 成本 0，消除误导。
- **路线 B（目标形态）**：真正启用 Nacos 配置中心 —— 修正 4 个配置文件、补 `has` 的配置、
  提供一键导入脚本、把公共配置（数据源/Redis/RabbitMQ 地址）抽到 `shared-config`。
  → 这才是"配置外置"的正解。

> 无论走哪条，**`core-dev.yaml` 的 application.name 写错这一条都该立刻修**——
> 万一有人照着文档导入配置，`core` 会以 `auth` 的名字注册到 Nacos，网关的 `lb://core` 直接 503。

### A-2 · 基础设施地址硬编码进 jar，配置没有真正外置 🔴

**事实**：各服务的 `bootstrap-{profile}.yaml` 里写死了 `magic-nacos:8848`、`magic-mysql:3306`、
`magic-redis:6379` 等。`local` 与 `dev` 的差异靠**两个不同的 yaml 文件**实现，
不是靠环境变量。

**影响**：换一套环境（另一台机器、换网段、未来上 K8s）必须**改代码重新打包**。
这也是"逐步迁移到可配置化"的**最大阻力**。

**优化建议**（这是最有价值的一条，因为它服务于"未来成本最低"的目标）

```yaml
# 改前：地址写死在 profile 文件里
server-addr: magic-nacos:8848

# 改后：地址来自环境变量，profile 文件只管"用哪个值"
server-addr: ${NACOS_ADDR:magic-nacos:8848}
```

具体做法：
1. 每个 profile 文件里，所有基础设施地址改成 `${XXX:默认值}` 形式；
2. `docker-compose*.yaml` 的 `environment` 段注入这些变量；
3. **profile 文件从 3 个收敛到 1 个**（`local`/`dev`/`prod` 的差异只剩业务开关，不含地址）；
4. `bootstrap.yaml` 只留 `spring.profiles.active`，地址一律外部注入。

**收益**：这一步做完，"上 K8s / 换环境 / 多套部署"就从"改代码"降级为"改环境变量"，
未来迁移成本最低。

### A-3 · 模块职责边界模糊

| 问题 | 位置 | 说明 |
|---|---|---|
| 同名 `VideoServiceImpl` 跨模块 | `core/.../VideoServiceImpl` 与 `multimedia/.../VideoServiceImpl` | 一个管影视信息 CRUD，一个管文件上传，**同名不同义**，读代码时极易误判 |
| 同一张表两个 Controller | `core` 的 `ResourceController` 与 `VideoSourceController` 都操作 `video_source` | `ResourceController` 实际注入的是 `VideoSourceService`，"resource" 与 "video_source" 是同一概念的两套叫法 |
| 命名与领域不一致 | `video_source` 存的是播放源；`ResourceDto` 传的是同一个东西 | Dubbo 契约 `ResourceService` 与实体 `VideoSource`、Controller `ResourceController` 三套叫法 |

**优化建议**：统一到 **"resource"** 这一个词（或统一到 "video_source"）。
建议以领域概念为准统一为 **resource**：
- 表名保持 `video_source` 不动（改表名成本高、收益低）；
- Java 侧统一叫 `Resource`：`ResourceController` / `ResourceService` / `ResourceDTO`；
- `VideoSourceController` / `VideoSourceService` 合并进 `ResourceController` / `ResourceService`；
- `multimedia` 的那个改叫 `MediaUploadService`，与 core 的 `VideoServiceImpl` 彻底区分。

### A-4 · 通信模型双轨，语义不统一

项目里同时存在两种服务间通信，各走各的：

| 通道 | 使用者 | 协议 | 特点 |
|---|---|---|---|
| Dubbo | gateway → auth（查权限）<br/>has → core（存资源） | Dubbo RPC | 强类型接口、有序列化开销、默认超时 1s / 重试 2 次 |
| HTTP | gateway → core / multimedia / auth | HTTP + JSON | 经网关路由 |

**问题**

1. **Dubbo 的超时/重试是默认值，项目没配**：
   - `bootstrap-{dev,local}.yaml` 的 `dubbo` 段只有 `application`/`protocol`/`registry`，
     **没有 `consumer.timeout` / `retries`** → 用 Dubbo 3.3.4 默认（timeout 1000ms、retries 2）；
   - auth 权限查询是多表 join，1s 偏紧；
   - **`saveResource` 语义非幂等，默认重试 2 次会产生重复写入**。
2. **两套错误语义**：HTTP 走 `Result<T>` + `GlobalExceptionHandler`；Dubbo 走异常直传，
   `NotImplementedException` 在 Dubbo 侧会被序列化成通用异常，前端拿到的是完全不同的报文。
3. **Dubbo 契约没有版本化**：`common-api` 里的接口直接改签名就破坏兼容，没有 `@DubboService(version=...)`。

**优化建议**

- **保留双轨，但明确边界**：HTTP 用于「外部请求驱动的 CRUD」（需要 REST 语义、需要网关路由）；
  Dubbo 用于「内部服务间的低频关键查询」（权限查询、资源回写）。
- **必须显式配置 Dubbo 超时与重试**：
  ```yaml
  dubbo:
    consumer:
      timeout: 3000
      retries: 0          # 权限查询可重试；saveResource 幂等前不重试
  ```
- **Dubbo 接口加版本号**：`@DubboService(version = "1.0.0")`，为后续演进留余地。

### A-5 · DDL 三套不一致

| 环境 | auth 表 | video 业务表 |
|---|---|---|
| `static/mysql/dev/` | 6 张 ✅ | 15 张 ✅ |
| `static/mysql/local/` | 6 张 ✅ | **仅 5 张** ❌ |
| `static/mysql/prod/` | **缺失** ❌ | **仅 5 张** ❌ |

缺 `category`、`district`、`tag`、`award`、`video_actor`、`video_director`、`video_scripter`、
`video_maker`、`video_category`、`video_district`、`video_tag`、`video_source` 之外的关联表。

**影响**：`local`（全容器体验模式）下访问 `/api/v1/core/category/all` 会报表不存在。

**优化建议**
1. **单一事实来源**：只维护一份 `DDL_video.sql`，用 `dev` 版本作为基线；
2. `local`/`prod` 用同一份脚本，只改数据库名与初始数据；
3. 引入迁移工具（Flyway / Liquibase）替代手工 DDL —— 这是**唯一能根治"多环境 schema 漂移"的办法**，
   而且成本不高：加一个 `db/migration/` 目录 + 一个依赖。

### A-6 · 77 个端点直接返回 Entity

`core` 的 11 个 Controller、`auth` 的 6 个 Controller 中，只有 3 个 Controller 做了 VO 转换
（`VideoController`、`SeasonController`、`CollectionController` + `ResourceController` 的分页接口）。

**具体危害**：`Award` / `Category` / `District` / `Tag` 四个实体**没有 `@JsonIgnore`**
（其余 core 实体有 6~8 个），因此 `deleted`（逻辑删除标记）、`revision`（乐观锁版本号）、
`createTime`、`updatedByUserId` 会直接输出给客户端 —— **内部实现细节成了 API 契约**。

**优化建议**

- 不追求一次性全改（那是灾难）。**按访问频率排优先级**：
  1. 先给所有 entity 补 `@JsonIgnore`（成本极低，立即消除字段泄漏）；
  2. 改高频读接口（`/video/list`、`/resource/*`）为 VO；
  3. 其余按需改。
- 抽一个统一的 Entity→VO 转换工具（如 `BeanCopyUtils`），避免手写 77 个 mapper。

### A-7 · 17 个 ServiceImpl 重复 608 行 CRUD 骨架

`findItemById` / `saveAndReturn` / `updateAndReturn` / `deleteAndReturnById` 四个方法
在 **17 个实现类**中重复出现（共 68 个方法声明、**608 行重复代码**）。
27 个 ServiceImpl 中有 17 个是这个模式。

**优化建议**：抽 `BaseCrudService<E, D>` 泛型抽象类，把四个方法收敛一次。

```java
public abstract class BaseCrudService<E, D> extends ServiceImpl<?, E> {
    protected abstract E toEntity(D dto);
    protected abstract D toDto(E entity);
    // 四个通用方法在这里实现一次
}
```

**注意**：抽取时**顺手把吞异常模式改掉**（P1-1）—— 否则只是把坏模式复制到一个基类里。

### A-8 · 可测试性缺失

7 个测试文件**全部**是 `@SpringBootTest` + `@ActiveProfiles("dev")` 的集成测试，
**0 个纯单元测试**。其中：

- `core/.../VideoResourceServiceTest.java` —— **空类，0 个 `@Test`**
- `has/.../HasApplicationTests.java` —— 唯一逻辑是 `System.out.println("Hello Hls")`
- `has/.../MediaKitServiceTest.java` —— **真跑 ffmpeg + 真连 MinIO**，且 URL 是 2025-03/04 的
  **硬编码且已过期**的签名地址

**最该补测试的 3 个类**（按性价比排序）

| 类 | 为什么 |
|---|---|
| `has/.../MediaKitServiceImpl` 的 `validateVideoUrl` / `validateFilename` / `getUrlFilename` | 纯函数分支（路径穿越、协议白名单、文件名格式），极易单测，且是**当前唯一没有测试覆盖的安全边界** |
| `auth/.../SysUserServiceImpl` | 密码强度校验、用户名正则、乐观锁行为；零外部依赖，Mockito 即可覆盖全部分支 |
| `multimedia/.../VideoServiceImpl`（`uploadVideoChunk` / `videoChunksMerge`） | 用 `@TempDir` + mock `MessageProducer`/`MinioClient`，**正好能覆盖 P0-5 的顺序缺陷** |

**建议**：先把这 3 个类补上纯单元测试（约 200 行），成本低、收益高，
且能在改 P0/P1 时提供回归保护。

---

## 五、演进路线（按"一步步可控"排）

> 原则：**每个阶段结束时项目都必须是可运行、可演示的状态**，不做大爆炸式重构。

### Phase 0 · 修断点（让主流程真正跑通）—— 建议 1~2 周

**目标**：上传 → 转码 → 落库 → 播放，这条链路端到端能跑通一次。

| 序 | 事项 | 类型 |
|---|---|---|
| 1 | P0-1 MQ 消息类型改用 `ResourceMessage` DTO | 代码 |
| 2 | P0-4 实现 `DubboResourceService` + `has` 侧接线 | 代码 |
| 3 | P0-5 拆分 merge / upload / send 三步，错误向上抛 | 代码 |
| 4 | P0-2 去掉 auth XML 的 `dev_auth.` 前缀 | 代码 |
| 5 | P0-3 auth XML 补 `deleted = 0` | 代码 |
| 6 | P0-6 ffmpeg 加超时 + 独立线程池 + 手动 ack | 代码 |

**验收标准**：上传一个 mp4 → 观察到 has 转码 → `video_source` 表出现 m3u8 记录 →
`/api/v1/core/resource/all` 能查到播放源。

### Phase 1 · 配置外置（为未来降低迁移成本）—— 建议 1 周

| 序 | 事项 |
|---|---|
| 1 | A-2：基础设施地址全部改成 `${ENV:默认值}` |
| 2 | profile 文件从 3 个收敛为 1 个 + 环境变量差异 |
| 3 | A-1：修正 `core-dev.yaml` 的 application.name（**无论选哪条路线都要做**）；补 `has` 配置 |
| 4 | 选 A-1 的路线 A 或 B 并在文档里写死 |

### Phase 2 · 数据一致性 —— 建议 1~2 周

| 序 | 事项 |
|---|---|
| 1 | P1-1：吞异常改为分类捕获 + 重新抛出未知异常 + 加 `@Transactional` |
| 2 | P1-2：修乐观锁调用形态 |
| 3 | P1-3：加 `@CacheEvict` |
| 4 | P1-5：修磁盘清理 + 让 `@Scheduled` 真正生效 |
| 5 | A-5：统一 DDL + 引入 Flyway |

### Phase 3 · 接口与结构整理 —— 建议 2 周

| 序 | 事项 |
|---|---|
| 1 | A-6：先补 `@JsonIgnore`，再改高频接口为 VO |
| 2 | A-7：抽 `BaseCrudService` |
| 3 | A-3：统一 resource 命名，合并 `ResourceController` / `VideoSourceController` |
| 4 | A-8：补 3 个核心类的纯单元测试 |

### Phase 4 · 安全加固 —— 随时可穿插

P2-1 MinIO 私有化 + 预签名 URL → P2-2 登录限速 → P2-3 生产配置去默认值 → P2-4 kick-out 权限细化

### 明确不做（与 [02-architecture.md](02-architecture.md) 的边界一致）

❌ K8s 集群部署 ❌ 服务网格 ❌ 分布式追踪 ❌ 集群限流熔断 ❌ 完全云化

> 上述所有建议都在**单机 docker-compose** 的约束内成立，不要求引入新中间件
> （Flyway 是唯一的新增建议，且它是纯 Java 库，不需要额外部署）。

---

## 附录 A · 依赖版本升级路线

**当前状态**：Boot `3.3.13` / Spring Cloud `2023.0.5` / SCA `2023.0.1.2` /
Gateway `4.1.5` / MyBatis-Plus `3.5.7` / Dubbo `3.3.4`。

**背景**：`spring-boot-starter-actuator` 存在未修复的 CVE-2026-22733（high，CVSS 8.2），
GitHub Dependabot 有 1 条 open 告警。官方公告中该漏洞的 OSS 修复版只有 **3.5.12+** 与 **4.0.4+**，
3.3.18 / 3.4.15 / 2.7.32 **仅企业版**。

**实际可利用性评估**：本项目**不可利用**——
① 零自定义 `@Endpoint`；② 5 个服务全部 `exclude: cloudfoundry`；③ `spring-security-web`
仅存在于 `auth` 模块，gateway 只有 Reactor 版 Sa-Token。

**已验证的升级路径**（沙盒实测：全量构建通过、core 服务启动成功、注册 Nacos 成功、
`/actuator/health` 返回 `{"status":"UP"}`、分页接口返回 `total:41, pages:14`）：

| 组件 | 当前 | 目标 |
|---|---|---|
| Spring Boot | 3.3.13 | **3.5.16** |
| Spring Cloud | 2023.0.5 | 2025.0.3 |
| Spring Cloud Gateway | 4.1.5 | 4.3.5 |
| Spring Cloud Alibaba | 2023.0.1.2 | 2025.0.0.0 |
| spring-cloud-starter-bootstrap | 4.1.3 | 4.3.3 |
| MyBatis-Plus | 3.5.7（`mybatis-plus-boot-starter`） | 3.5.18（**`mybatis-plus-spring-boot3-starter`** + `mybatis-plus-jsqlparser`） |
| mybatis-spring | 2.1.2 | 3.0.6 |
| nacos-client | 2.5.0（dubbo 拖入） | **3.0.3（需在根 pom `dependencyManagement` 显式锁定）** |
| io.minio | 8.6.0 | 保持（8.6.1 在当前镜像源无法解析） |

**升级中踩到的 5 个坑**（都已在沙盒解决）

1. **根 pom 显式 pin 了 `spring-actuator.version` 等 5 个属性为 3.3.13**，覆盖 Boot BOM →
   即使升级 Boot，actuator 仍解析为 3.3.13，**告警消不掉**。必须删掉这些属性与
   `dependencyManagement` 中 4 个冗余 starter 条目。
2. `factoryBeanObjectType: java.lang.String` —— MyBatis-Plus 3.5.7 + mybatis-spring 2.1.2
   与 Boot 3.5.x 不兼容 → starter 换成 `mybatis-plus-spring-boot3-starter`。
3. **MP 3.5.9+ 拆分 artifact** —— `ServiceImpl`/`IService` 迁到
   `com.baomidou.mybatisplus.spring.service.*`（**21 个文件各 2 处 import**）；
   `PaginationInnerInterceptor` 移到独立 artifact `mybatis-plus-jsqlparser`；
   `Page` 仍在 `com.baomidou.mybatisplus.extension.plugins.pagination.Page`（**不要跟着改**）。
4. `dubbo-registry-nacos:3.3.4` 拖入 `nacos-client:2.5.0`，与 SCA 2025 期望的 3.0.3 冲突 → 需锁定。
5. 升级后会暴露当前代码里的两个缓存 bug（`unless` 表达式对 `Optional` 求值、
   派生只读属性反序列化失败）—— **这两个已在主项目修好**（见 ADR-0003），升级时无需再处理。

**建议**：Boot 大版本升级是**独立于本审查的一次变更**，不与业务修复混在一起。
在 Phase 0~1 完成、代码处于稳定状态后，单独开一个 PR 做。

---

## 附录 B · 验证方式说明

| 结论类型 | 验证手段 |
|---|---|
| 硬编码 `dev_auth.`、缺 `deleted = 0`、吞异常、`@Scheduled` 缺失、`mediaExecutor` 死代码 | 直接读源码 + `grep` 计数，位置精确到行号 |
| MQ 消息类型冲突 | **运行时实证**：用项目真实依赖（spring-amqp 3.1.12 + jackson-databind 2.17.3）跑 `toMessage`/`fromMessage` 往返，复现 `IllegalArgumentException` |
| 乐观锁失效 | 源码确认调用形态为 `update(wrapper)` → `baseMapper.update(null, wrapper)` → 实体为 null → `OptimisticLockerInnerInterceptor` 不追加版本条件（已 `javap` 核对 MP 3.5.7 字节码） |
| Spring Cache 对 `Optional` 的解包行为 | 已 `javap` 核对 `spring-context` 的 `CacheAspectSupport.unwrapReturnValue`（详见 ADR-0003） |

> 本次审查**未修改任何业务代码**，仅新增文档。所有定位均基于审查时的代码状态。
