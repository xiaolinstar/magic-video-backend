# 09 · 技术债务清单

> 所有条目按"如果今天不修，明天会不会出问题"分级。
> 状态：🟢 待修  |  🛠 修中  |  ✅ 已修
> 每项给出 ADR 或审查报告链接说明"打算怎么做"或"为什么先不做"。

**最近一次全面复核**：2026-11-08（初版复核见 [`10-architecture-review.md`](10-architecture-review.md)）

- 2026-10-01 复核：修正 5 处失效的 ADR 链接（原写作 `99-adr/`，实际目录为 `adr/`），并移除了已修复条目。
- 2026-11-08 复核：Phase 0 的 9 个 P0 与 P1-4 / P1-10 / P1-11 全部关闭并实证；
  新增 **P1-13**（生产编排缺 `multimedia` / `has` / MinIO）。

---

## P0 · 必须先修（业务跑不通的前提）

### P0-1 · MQ 消息类型与 Jackson 可信包白名单冲突 —— 转码链路从未跑通 ✅ 已修

**位置**：
- `common/common-message/.../MessageProducerImpl.java:43` — 发送 `java.net.URL` 对象
- `common/common-message/.../RabbitMQConfig.java:32-46` — 可信包仅 `cn.xiaolin.message.entity` / `cn.xiaolin.message.dto`
- `has/.../MessageConsumerImpl.java:29` — 按 `URL` 接收

**实证**：用项目真实依赖跑往返 → `IllegalArgumentException: The class 'java.net.URL' is not in the trusted packages`

**影响**：消费端转换失败 → AUTO ack 失败 + `defaultRequeue=true` → **毒消息无限重投，`has` 空转**，视频永不转码。

**修复方向**：改用 `ResourceMessage` DTO 传输（已在白名单内，已有现成类型）。

详见 [`10-architecture-review.md` P0-1](10-architecture-review.md)

---

### P0-2 · auth 自定义 SQL 硬编码库名 `dev_auth.` ✅ 已修

**位置**：`auth/src/main/resources/mapper/SysUserMapper.xml`（9 处）、`SysRoleMapper.xml`（2 处）

**影响**：auth 的 local profile 数据源是 `local_auth`、dev 是 `dev_auth`。
因此在 **local 与 prod 环境，登录入口 `getUserByUsername` 与 Dubbo 权限下发 `listRolesByUserId` 必然报错**。

**修复方向**：去掉库名前缀，统一用裸表名（与 `SysPermissionMapper.xml` 对齐）。

详见 [`10-architecture-review.md` P0-2](10-architecture-review.md)

---

### P0-3 · 逻辑删除被自定义 SQL 绕过 —— 已注销用户仍可登录 ✅ 已修

**位置**：`auth/src/main/resources/mapper/` 下全部 6 个 XML，**一个 `deleted = 0` 条件都没有**

**影响**：`@TableLogic` 只对 MP 自动生成的 SQL 生效。已删除用户仍能登录、已删权限仍下发网关、已删角色仍生效。

**修复方向**：所有自定义 `<select>` 补 `deleted = 0`（关联表也要补）。

详见 [`10-architecture-review.md` P0-3](10-architecture-review.md)

---

### P0-4 · `DubboResourceService` 未实现 + 消费方未接线 ✅ 已修

**位置**：
- `core/.../DubboResourceService.java:18,23` — 两个方法均抛 `NotImplementedException`
- `has/.../DubboResourceClient.java` — **全仓无任何调用点**

**影响**：转码产物永远不会写进 `video_source` 表，播放源查不到。**落库通道两端都断。**

**修复方向**：
1. `core` 实现 `saveResource` → 写 `video_source`，`getResourceById` → 读 `video_source`
2. `has` 在转码成功后调用 `DubboResourceClient.saveResource(...)`

详见 [`10-architecture-review.md` P0-4](10-architecture-review.md)

---

### P0-5 · multimedia「合并 → 上传 → 发消息」被一个 `if` 包住 ✅ 已修

**位置**：`multimedia/.../service/impl/VideoServiceImpl.java:234`

```java
if (!targetFile.exists()) {   // 合并、上传、发消息 全在这个 if 内
```

**影响**：重复调用 merge → 整段跳过 → **MQ 消息永不补发**，视频永远不被转码。
发消息异常还被 `catch (MalformedURLException)` 吞掉，静默丢失。

**修复方向**：只让"合并"步骤受 `exists` 判断保护；发消息失败向上抛。

详见 [`10-architecture-review.md` P0-5](10-architecture-review.md)

---

### P0-6 · ffmpeg 同步阻塞且占用唯一 MQ 消费线程 ✅ 已修

**位置**：`has/.../MediaKitServiceImpl.java:99-135, 263-305`（`ProcessBuilder.waitFor()` 无 timeout）
+ `has/.../MessageConsumerImpl.java:28`（无 concurrency、无 ackMode）

**影响**：Spring AMQP 默认 concurrency=1 → **整个 has 服务卡死在一个视频上**；网络挂起则线程永久占用。

**修复方向**：独立线程池 + `waitFor(timeout)` + `destroyForcibly()` + 手动 ack。

详见 [ADR-0006](adr/0006-async-transcoding.md)

---

### P0-7 · Controller 增删改大面积 NotImplemented 🟢

**位置**（11 处）：

| 位置 | 端点 |
|---|---|
| `auth/.../SysUserController.java:70` | `GET /user/perm/{id}` |
| `auth/.../SysUserController.java:76` | `GET /user/role/{id}` |
| `core/.../ResourceController.java:100,107,114,121` | `/resource/{id}` 的 GET/POST/PUT/DELETE |
| `multimedia/.../VideoController.java:44` | `GET /video/check` |
| `multimedia/.../ImageServiceImpl.java:95` | `imageUpload(String base64)` |
| `common/message/.../MessageProducerImpl.java:32,37` | `publish` 的两个重载 |

**修复方向**：分批实现，优先级：
1. `ResourceController` CRUD（业务核心，且是 P0-4 落库链路的入口）
2. `SysUserController` perm/role 查询（auth 网关依赖）
3. `MessageProducer` exchange/routingKey 重载
4. `ImageService` base64 上传

---

### P0-8 · MyBatis-Plus starter 用错变体 —— **全项目无法启动** ✅ 已修

**位置**：根 `pom.xml` + `common/common-db/pom.xml`

```xml
<artifactId>mybatis-plus-boot-starter</artifactId>   <!-- 这是 Boot 2 变体 -->
```

**问题**：`mybatis-plus-boot-starter` 引入的是 `mybatis-spring:2.1.2`，
其 `ClassPathMapperScanner` 会把 `factoryBeanObjectType` 属性设为字符串 `"java.lang.String"`；
而 **Spring 6.1+ 对该属性做了严格校验**，服务在启动时直接失败：

```
Invalid bean definition with name 'sysRolePermissionMapper' ...:
Invalid value type for attribute 'factoryBeanObjectType': java.lang.String
```

**影响**：**5 个服务全部无法启动**。这不是本次改动引入的 —— 该校验在 Spring 6.1.15
（即 Boot 3.2.12 使用的版本）中就已存在，说明项目自 Boot 2→3 迁移起就从未真正跑起来过。
这也是「主链路从未端到端验证过」的根本原因。

**修复方向**：改用 Boot 3 专用变体 `mybatis-plus-spring-boot3-starter`
（引入 `mybatis-spring:3.0.3`，已修复该问题）。版本保持 3.5.7 不变，
现有 `com.baomidou.mybatisplus.extension.service.impl.ServiceImpl` 等 import 全部有效。

---

### P0-9 · 历史弱口令迁移触发强度校验 —— **auth 服务启动失败** ✅ 已修

**位置**：`auth/.../config/AppConfig.java`（启动时的明文密码迁移）

**问题**：迁移逻辑复用了 `sysUserService.updateAndReturn(dto)`，
而该方法会对密码执行 `validatePasswordStrength`。库里遗留的明文密码
（如 `123456xxl`）不满足「大小写字母 + 数字 + 符号至少三类」，于是
`CommandLineRunner` 抛异常 → **auth 服务启动直接失败**。

**修复方向**：新增 `SysUserService.resetPasswordWithoutStrengthCheck(id, rawPassword)`，
仅供「历史数据迁移」这类内部场景使用；强度规则的目的是约束**用户的新输入**，
不该用来约束存量数据。`AppConfig` 改用该方法，并对空密码 / 加密失败做容错。

---

## P1 · 应该尽快修（影响稳定性）

### P1-1 · 全模块零事务 + 17 处吞异常 🟢

**事实**：4 个模块 `@Transactional` 用量为 **0**；`TransactionTemplate` 出现在 15 个 ServiceImpl 中但**零使用**（死 import）。
17 处统一的 `catch (Exception e) { log.error(e.getMessage()); }`。

**影响**：唯一键冲突、连接池耗尽等服务端故障被降级为 `Optional.empty()` → Controller 统一映射为 **HTTP 400**。
**服务端 500 被伪装成客户端 400**，且堆栈丢失难以定位。

**修复方向**：分类捕获 + 未知异常重新抛出 + `log.error("上下文", e)` + 多表写入加 `@Transactional(rollbackFor = Exception.class)`。

详见 [`10-architecture-review.md` P1-1](10-architecture-review.md)

---

### P1-2 · 乐观锁形同虚设 🟢

**位置**：所有更新路径走 `update(updateWrapper)`（如 `core/AwardServiceImpl.java:78`）
→ 底层 `baseMapper.update(null, wrapper)`，**实体为 null**
→ `OptimisticLockerInnerInterceptor` 不追加版本条件（已 `javap` 核对字节码）

**影响**：21 个 entity 都标了 `@Version`，但并发更新**互相覆盖，无冲突检测**。

**修复方向**：传带版本号的实体，或 wrapper 里显式 `setSql("revision = revision + 1")` + `eq(版本字段, dto.getRevision())`。

---

### P1-3 · 缓存无失效策略 🟢

**位置**：`core/.../VideoServiceImpl.java:47` 有 `@Cacheable(value = "video", key = "#id")`，
全仓 **0 处 `@CacheEvict` / `@CachePut`**。

**影响**：视频更新/删除后最长 10 分钟（TTL）返回旧数据。

**修复方向**：更新/删除方法加 `@CacheEvict(value = "video", key = "#id")`。

---

### P1-4 · RabbitMQ 消费者无 ack / 无死信队列 / 无并发度 ✅ 已修

**位置**：`has/.../MessageConsumerImpl.java:28`（`@RabbitListener` 只有 `queues`）
+ `common/common-message/.../RabbitMQConfig.java:62-65`（`new Queue(...)` 未设 `x-dead-letter-exchange`）
+ 4 个模块 yaml 中无任何 `spring.rabbitmq.listener.*`

**影响**：失败消息被无限 requeue；重复投递会重复转码并覆盖 MinIO 上传。

详见 [ADR-0007](adr/0007-rabbitmq-consumer-ack.md)

---

### P1-5 · 磁盘不清理 + 定时任务是死代码 🟢

| 位置 | 现象 |
|---|---|
| `multimedia/.../VideoServiceImpl.java:171-200, 232-266` | 分片目录与合并文件上传后不删除 |
| `multimedia/.../VideoServiceImpl.java:204-207` | `abortUpload` 直接 `return false`，不做清理 |
| `has/.../MediaKitServiceImpl.java:83-94, 247-258` | `~/hls-video/`、`~/dash-video/` 产物从不删除 |
| `multimedia/.../config/ScheduleTask.java:18` | `@EnableScheduling` 开了，但 `videoResourceScan()` **没有 `@Scheduled` 注解** → **定时任务从未运行** |
| `multimedia/.../config/ThreadPoolConfig.java:31` | `mediaExecutor` 线程池只在定义处 + `shutdown()` 出现 → **死代码** |

**修复方向**：转码完成即删临时目录；给 `videoResourceScan` 真正加上 `@Scheduled(cron = ...)`；
把 `mediaExecutor` 用起来（P0-6 的转码异步化正好需要它）。

---

### P1-6 · DASH 转码参数自相矛盾 🟢

**位置**：`has/.../MediaKitServiceImpl.java:267-272`

`-map 0:v:0 -map 0:a:0` 重复 3 次却配了 3 组码率参数 → **实际只输出 1 路，第 2、3 组静默失效**。

---

### P1-7 · ResourceController 推荐位无业务逻辑 🟢

**位置**：`core/.../ResourceController.java`

`/resource/recommend` `/banner` `/latest` `/classic` `/featured` **5 个端点调用完全相同**，
均按 videoId 倒序返回，无任何场景区分。

**修复方向**：
- 在 `video_source` 表加 `featured` / `banner` 标记字段；
- 或新增 `VideoSourceService.listSourcePageByType(type, pageQuery)`。

---

### P1-8 · Service 层返回 Entity 直接出参 🟢

**事实**：**77 个端点**直接返回持久化实体（core 11 个 Controller + auth 6 个 Controller），
仅 3 个 Controller 做了 VO 转换。

**具体危害**：`Award` / `Category` / `District` / `Tag` 四个实体 `@JsonIgnore` 数量为 0，
`deleted`（逻辑删除标记）、`revision`（乐观锁版本号）等内部字段会输出给客户端。

**修复方向**：
1. 先给所有 entity 补 `@JsonIgnore`（成本极低，立即消除字段泄漏）；
2. 改高频读接口为 VO；
3. 抽统一的 Entity→VO 转换工具。

详见 [`10-architecture-review.md` A-6](10-architecture-review.md)

---

### P1-9 · HealthCheck 三层探针未分开 🟢

**位置**：各服务的 `application.yaml`

**问题**：`livenessProbe` 与 `readinessProbe` 都指向 `/actuator/health`，启动期间会被 liveness 误杀。

**修复方向**：启动探针用 `/actuator/health/liveness`，就绪探针用 `/actuator/health/readiness`。

---

### P1-10 · Nacos 配置中心名不副实 + 配置数据有误 ✅ 已修

**位置**：`static/nacos/DEFAULT_GROUP/*.yaml` + 各服务 `bootstrap-{local,dev}.yaml`

**事实**：
- `local`/`dev` 全部 `nacos.config.enabled: false`，**只有 prod 启用**；
- prod 的配置数据需人工导入 Nacos，无脚本无文档；
- `core-dev.yaml` 的 `spring.application.name` **误写为 `auth`**；
- `core-dev.yaml` 指向 `magic-dev-mysql` / `magic-dev-redis` —— **这两个容器名在 compose 中不存在**；
- `has` 服务**完全没有** Nacos 配置文件。

**影响**：一旦照着这些文件导入配置，`core` 会以 `auth` 的名字注册 → 网关 `lb://core` 直接 503。

**修复方向**（二选一，别停在中间态）：
- **路线 A**：承认当前没有配置中心，删除/标注 `static/nacos/`，README 写清"配置在 jar 内"；
- **路线 B**：真正启用 —— 修正 4 个文件 + 补 `has` 配置 + 一键导入脚本 + 抽公共配置。

**无论走哪条，`core-dev.yaml` 的 application.name 都要立刻修。**

---

### P1-11 · 基础设施地址硬编码进 jar ✅ 已修

**位置**：各服务 `bootstrap-{local,dev,prod}.yaml`

**事实**：地址写死为 `magic-nacos:8848` / `magic-mysql:3306` / `magic-redis:6379` 等，
`local` 与 `dev` 的差异靠**两份不同的 yaml** 而非环境变量。

**影响**：换环境必须改代码重新打包 —— 这是"未来迁移成本最低"目标的**最大阻力**。

**修复方向**：
1. 所有基础设施地址改成 `${NACOS_ADDR:magic-nacos:8848}` 形式；
2. compose 的 `environment` 段注入这些变量；
3. profile 文件从 3 个收敛为 1 个。

详见 [`10-architecture-review.md` A-2](10-architecture-review.md)

---

### P1-12 · DDL 三套不一致 🟢

| 环境 | auth 表 | video 业务表 |
|---|---|---|
| `static/mysql/dev/` | 6 张 ✅ | 15 张 ✅ |
| `static/mysql/local/` | 6 张 ✅ | **仅 5 张** ❌ |
| `static/mysql/prod/` | **缺失** ❌ | **仅 5 张** ❌ |

**影响**：local 模式访问 `/api/v1/core/category/all` 会报表不存在。

**修复方向**：单一事实来源（以 dev 版为基线）+ 引入 Flyway/Liquibase 管理迁移。

---

### P1-13 · 生产编排缺 3 个服务 —— prod 网关的 multimedia 路由必然 503 🟢

**位置**：`mini-local.yaml`（生产编排）

**事实**：生产编排只包含 `nginx` / `mysql` / `magic-auth-redis` / `magic-redis` /
`magic-rabbitmq` / `magic-auth` / `magic-gateway` / `magic-core` —— **缺 `magic-multimedia`、
`magic-has`、以及两个 MinIO**（`docker-compose-local.yaml` 里全都有）。

**影响**：

1. `gateway/.../bootstrap-prod.yaml` 里的 multimedia 路由 `http://magic-multimedia:9002`
   在 prod 下 **DNS 解析失败** → 上传相关接口必 503；
2. `has`（转码）在 prod 下**根本没有实例** → 转码链路不可用；
3. `multimedia` / `has` 的 prod profile 里的 `magic-multimedia-minio` / `magic-has-minio`
   同样解析不到 → 即使把服务拉起来也连不上对象存储。

**已做**：2026-11-08 补齐了 `has/src/main/resources/bootstrap-prod.yaml`
（此前 5 个服务里只有它缺 prod profile，且它从未有过 Nacos 配置）。

**修复方向**：把 `magic-multimedia` / `magic-has` / 两个 MinIO 从 `docker-compose-local.yaml`
迁到 `mini-local.yaml`，并核对 prod 侧的 MinIO 密钥与 bucket 初始化。

---

## P2 · 安全相关

### P2-1 · MinIO bucket 公共可读 🟢

**位置**：`multimedia/config/AppConfig.java:66-72`

**问题**：bucket 策略含 `"Action":["s3:GetObject"]` + `"Principal":{"AWS":["*"]}` → 任何人拿到 URL 即可下载原片。

**修复方向**：bucket 改私有 + 后端下发**预签名 URL**（短有效期）；或按 bucket 区分（缩略图公开、原片私有）。

---

### P2-2 · `DubboUserPermClient` 每次请求都查 DB 🟢

**位置**：`gateway/.../dubbo/consumer/DubboUserPermClient.java`

**问题**：Sa-Token 每次 HTTP 请求都调 `getPermissionList` / `getRoleList`，每次都走 Dubbo → MySQL。

**修复方向**：角色/权限加 Redis 或 Caffeine 缓存（TTL 5 分钟）。

---

### P2-3 · `login` 接口无失败限速 🟢

**位置**：`auth/.../service/impl/SysUserServiceImpl.java:122-130`

**修复方向**：Redis 计数，按「用户名 + IP」5 次失败锁 5 分钟。

---

### P2-4 · 敏感配置默认值明文入库 🟢

**位置**：各 `bootstrap-*.yaml`（MySQL `123456`、RabbitMQ `123456rabbit`、MinIO 账号密钥）

**现状**：已支持环境变量覆盖，且仓库已开启 `secret_scanning` 与
`secret_scanning_push_protection`；历史 `.env.local` 仅含占位符，**无真实密钥泄露**。

**修复方向**：生产 profile 不提供默认值（`${MYSQL_PASSWORD}`，缺失即启动失败）。

---

## P3 · 优化项（可维护性）

### P3-1 · 17 个 ServiceImpl 重复 608 行 CRUD 骨架 🟢

**事实**：`findItemById` / `saveAndReturn` / `updateAndReturn` / `deleteAndReturnById`
在 17 个实现类中重复（共 68 个方法声明、608 行）。27 个 ServiceImpl 中有 17 个是这个模式。

**修复方向**：抽 `BaseCrudService<E, D>` 泛型抽象类。
**注意**：抽取时顺手把吞异常模式改掉，否则只是把坏模式复制到基类。

---

### P3-2 · CollectionServiceImpl.getSlideList 用 subList 🟢

**位置**：`core/.../service/impl/CollectionServiceImpl.java:99-104`

```java
List<Video> videoList = videoService.list();   // 全表扫
List<Video> videos = videoList.subList(0, Math.min(videoList.size(), 5));
```

**修复方向**：DB 端 `LIMIT 5`。

---

### P3-3 · 模块职责边界模糊（命名冲突）🟢

| 问题 | 位置 |
|---|---|
| 同名 `VideoServiceImpl` 跨模块（一个管 CRUD、一个管文件上传） | `core` / `multimedia` |
| 同一张表两个 Controller（`ResourceController` 与 `VideoSourceController` 都操作 `video_source`） | `core` |
| 同一概念三套叫法（`ResourceService` / `VideoSourceService` / `video_source` 表） | `core` + `common-api` |

**修复方向**：统一叫 "resource"，合并 Controller/Service，`multimedia` 那个改叫 `MediaUploadService`。

---

### P3-4 · Dubbo 契约无版本化 + 超时重试未配置 🟢

**事实**：`bootstrap-{dev,local}.yaml` 的 `dubbo` 段只有 `application`/`protocol`/`registry`，
**没有 `consumer.timeout` / `retries`** → 用 Dubbo 3.3.4 默认（timeout 1000ms、retries 2）。
`saveResource` 语义非幂等，默认重试 2 次会产生重复写入。

**修复方向**：显式配置超时与重试；`@DubboService(version = "1.0.0")`。

---

### P3-5 · 单元测试几乎为零 🟢

**事实**：7 个测试文件**全部**是 `@SpringBootTest` + `@ActiveProfiles("dev")` 的集成测试，
**0 个纯单元测试**。其中 `core/.../VideoResourceServiceTest.java` 是**空类**，
`has/.../HasApplicationTests.java` 唯一逻辑是 `System.out.println`，
`has/.../MediaKitServiceTest.java` 用的是 2025-03/04 的**硬编码且已过期**的签名 URL。

**最该补的 3 个类**：
1. `has/.../MediaKitServiceImpl` 的 `validateVideoUrl` / `validateFilename` / `getUrlFilename`
   （纯函数、当前唯一无测试覆盖的安全边界）
2. `auth/.../SysUserServiceImpl`（密码强度、用户名正则、乐观锁）
3. `multimedia/.../VideoServiceImpl`（`uploadVideoChunk` / `videoChunksMerge`，能覆盖 P0-5）

---

### P3-6 · DTO 上误用 `@TableField` `@TableId` ✅

**位置**：
- `core/.../domain/dto/VideoReqDto.java`、`VideoMakerReqDto.java`（已清理）
- `auth/.../domain/dto/SysPermReqDto.java` 等（已清理）

**状态**：✅ 已在 P0 修复阶段清理完毕。

---

## P4 · 已完成（留档）

| 项 | 落地方式 |
|---|---|
| 缓存 TTL 笔误（`ofMillis(10L)` / `ofMillis(30L)`） | [ADR-0003](adr/0003-cache-strategy.md) |
| `@EnableCaching` 从未启用 | [ADR-0003](adr/0003-cache-strategy.md) |
| 缓存值用 `Optional` 包装导致命中即报错 | [ADR-0003](adr/0003-cache-strategy.md) |
| Jackson 反序列化未知字段（派生只读属性） | [ADR-0003](adr/0003-cache-strategy.md) |
| Sa-Token 白名单含 `/auth/logout` `/auth/kick-out` | [ADR-0004](adr/0004-sa-token-public-route.md) |
| MinIO 对象 key 使用用户原始文件名 | [ADR-0005](adr/0005-minio-object-key.md) |
| 列表接口全表查询 | [ADR-0002](adr/0002-pagination.md) |
| entity 主键未指定策略 | [ADR-0001](adr/0001-snowflake-id.md) |
| `RabbitMQConfig` 编译错误（`setTrustedPackages` 已移除） | 改用 `Jackson2JsonMessageConverter` 构造器 |
| `auth` 模块缺 `spring-boot-starter-security` | 补依赖 |

---

## P5 · 暂不做

| 决策 | 不做的理由 |
|---|---|
| K8s 集群部署 | 单机 docker-compose 够用 |
| 服务网格 (Istio) | 单机不需要 |
| 分布式追踪 (Jaeger/Tempo) | 单机日志排障够用 |
| 集群限流 / 熔断 | 单点 + Nginx 限速足够 |
| **统一的** Spring Boot Admin 聚合监控 | Prometheus + Grafana 替代 |
| 引入新的中间件（Redis Cluster / Elasticsearch / Zipkin 等） | 单机场景收益低于运维成本 |

---

## 修复进度跟踪

### Phase 0 · 修断点（让主流程真正跑通）—— ✅ 已完成 2026-11-08

- [x] P0-1：MQ 消息类型改用受信包内 DTO（`TranscodeMessage`）
- [x] P0-2：去掉 auth XML 的 `dev_auth.` 前缀
- [x] P0-3：auth XML 补 `deleted = 0`
- [x] P0-4：实现 `DubboResourceService` + has 侧接线
- [x] P0-5：拆分 merge / upload / send
- [x] P0-6：ffmpeg 超时 + 手动 ack + 死信队列 + 幂等
- [x] P0-7：`MessageProducer` 的两个 `publish` 重载（其余 Controller 端点仍待修）
- [x] P0-8：MyBatis-Plus 换 Boot 3 专用 starter（**全项目恢复可启动**）
- [x] P0-9：历史弱口令迁移绕开强度校验
- [x] P1-4：RabbitMQ 手动 ack + 死信队列 + 并发度
- [x] P1-10 / P1-11：移除 Nacos，配置与地址显式化

**新增的自动化测试**（均为真实中间件集成测试）：

| 测试 | 覆盖 |
|---|---|
| `TranscodeMessageContractTest` | 消息经真实 RabbitMQ 往返，可信包校验不再失败 |
| `DeadLetterRoutingTest` | 消费失败的消息进入死信队列，原队列不残留 |
| `DubboResourceServiceTest` | 播放源落库 3 行、重复回写幂等、读回还原 |

### Phase 1 · 配置外置 —— ✅ 已完成 2026-11-08（随 Nacos 移除一并落地）

- [x] P1-11：基础设施地址在各服务 `bootstrap-{profile}.yaml` 显式给出
- [x] P1-10：删除 `static/nacos/`（含 4 份内容错误的配置），补全 prod profile
- [x] 补建 `static/mysql/prod/DDL_auth.sql`
- [x] 补建 `has/src/main/resources/bootstrap-prod.yaml`（5 个服务里唯一缺 prod profile 的）
- [x] 网关 prod 路由补齐（原先路由只存在于 Nacos，移除后必须落到 yaml）

### Phase 2 · 数据一致性 —— 🟢 待做

- [ ] P1-1：吞异常 + 补事务
- [ ] P1-2：修乐观锁
- [ ] P1-3：加 `@CacheEvict`
- [ ] P1-5：修磁盘清理 + 让 `@Scheduled` 生效
- [ ] P1-12：统一 DDL + 引入 Flyway

### Phase 3 · 接口与结构整理 —— 🟢 待做

- [ ] P1-8：Entity → VO
- [ ] P3-1：抽 `BaseCrudService`
- [ ] P3-3：统一 resource 命名
- [ ] P3-5：补更多单元测试

### Phase 4 · 安全加固 —— 🟢 待做

- [ ] P2-1：MinIO 私有化 + 预签名 URL
- [ ] P2-2：权限缓存
- [ ] P2-3：登录限速
- [ ] P2-4：生产配置去默认值

### Phase 5 · 生产部署补全 —— 🟢 待做

- [ ] P1-13：`mini-local.yaml` 补 `magic-multimedia` / `magic-has` / 两个 MinIO
- [ ] prod 侧 MinIO 密钥与 bucket 初始化核对
- [ ] prod profile 端到端验证（目前 prod 只有 auth/core/gateway 被实际跑通）
