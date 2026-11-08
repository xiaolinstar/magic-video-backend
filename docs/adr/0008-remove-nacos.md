# ADR-0008 · 移除 Nacos：服务发现与配置管理改用直连 + 本地配置

- 状态：✅ 已落地
- 日期：2026-11-08
- 关联：技术债 P1-10 / P1-11；架构审查 A-1 / A-2

---

## 背景

项目原本同时依赖 Nacos 的**三项**职责：

| 职责 | 实际使用情况 | 实际价值 |
|---|---|---|
| 配置中心 | `local`/`dev` profile 全部 `enabled: false`，**只有 `prod` 启用** | ≈ 0（且 prod 那 4 份配置内容是错的） |
| Spring Cloud 服务发现 | `lb://core` 形式的路由 | ≈ 0（所有部署形态都是**单实例**） |
| Dubbo 服务注册 | `registry.address: nacos://...` | ≈ 0（同上） |

审查中发现的关键事实：

1. **配置中心名不副实**：`static/nacos/DEFAULT_GROUP/` 下的配置从未真正生效
   - `core-dev.yaml` 的 `spring.application.name` **误写为 `auth`** —— 照此导入，`core`
     会以 `auth` 的名字注册到 Nacos，网关的 `lb://core` 直接 503；
   - `core-dev.yaml` 指向 `magic-dev-mysql` / `magic-dev-redis`，
     这两个容器名在 `docker-compose-dev.yaml` 中**不存在**；
   - `gateway-dev.yaml` 与 `core-dev.yaml` 的 Redis 地址互相不一致；
   - `has` 服务**完全没有**对应的 Nacos 配置。
2. **服务发现在单实例下没有意义**：local/prod 每个服务各 1 个实例，
   dev 模式下服务直接跑在 IDEA 里、根本不在容器网络中。
3. **多了一个必须常驻的中间件**：Nacos 挂了 = 服务发现失败 + 配置拉取失败。

### 为什么不是「上 K8s 后用 K8s 原生能力替换 Nacos」

这个方案技术上可行（Dubbo 3.x 原生支持 `kubernetes://` 注册中心，Spring Cloud Gateway
可走 K8s Service DNS），但对本项目有三处硬阻碍：

1. **dev 模式与 K8s 原生服务发现互斥**：微服务跑在 IDEA（宿主机）里，
   K8s Service/DNS 只在集群内有效。除非放弃「IDEA 里跑服务」这个开发体验，
   否则服务发现必须有一层跨宿主机/跨集群的方案。
2. **Dubbo 随机端口与 K8s Service 固定端口冲突**：项目原本 `dubbo.protocol.port: -1`
   （随机端口），随机端口无法用固定 `targetPort` 的 Service 暴露。
3. **与既有部署决策冲突**：本项目已明确「不上 K8s 集群」（单机 docker-compose 足够，
   集群运维要求与成本过高），且现有 `k8s/` 下的 manifest 全部过时损坏。

**关键判断：删掉 Nacos 的根本原因不是「没有 K8s」，而是「单实例下服务发现收益为零」。**
即使将来上 K8s，代码层也应该已经是「无注册中心化」的，届时只需替换地址注入方式。

---

## 决策

**路线 B：彻底移除 Nacos，不上 K8s。**

### 1. 依赖层：删除全部 Nacos 依赖

| 位置 | 移除内容 |
|---|---|
| 根 `pom.xml` | `spring-cloud-starter-alibaba-nacos-config`、`-discovery`、`dubbo-registry-nacos` 及其版本属性 |
| `common/common-utils/pom.xml` | `spring-cloud-starter-alibaba-nacos-config`、`-discovery` |
| `common/common-api/pom.xml` | `dubbo-registry-nacos` |

连带移除 `nacos-client:2.5.0` 及其 6 个传递依赖（auth-plugin / encryption-plugin /
logback-adapter / log4j2-adapter 等），以及 `alibabacloud-gateway-spi`。

### 2. Spring Cloud 服务发现 → 直连地址

网关路由由 `lb://<service>` 改为**直连地址**：

```yaml
routes:
  - id: auth
    uri: http://magic-auth:8999     # local；dev 为 http://localhost:8999
    filters:
      - PrefixPath=/api/v1
    predicates:
      - Path=/auth/**
```

`prod` profile 原本**没有路由**（路由在 Nacos 里），现已补齐到 `bootstrap-prod.yaml`。

### 3. Dubbo 服务注册 → 无注册中心直连

- Dubbo 3.3.4 **支持完全不配置注册中心**（官方：「3.3.0 及之后的版本可不配置注册中心」）；
- 提供方改为**固定协议端口**（随机端口无法被直连消费方寻址）：

  | 服务 | Dubbo 端口 | 角色 |
  |---|---|---|
  | `auth` | 20880 | `UserPermService` 提供方 |
  | `core` | 20881 | `ResourceService` 提供方 |
  | `gateway` | 20882 | 消费方 |
  | `has` | 20883 | 消费方 |

- 消费方通过 `@DubboReference(url = "dubbo://${dubbo.<peer>.host}:${dubbo.<peer>.port}")` 直连，
  地址在各 profile 的 bootstrap 文件中按环境给出：

```yaml
dubbo:
  # 直连 auth 的权限服务（不使用注册中心）
  auth:
    host: magic-auth      # dev: localhost
    port: 20880
```

### 4. 配置管理 → 保留在各服务的 bootstrap 文件

- 删除 `static/nacos/` 目录（含 4 份错误的配置）；
- `prod` profile 原先完全依赖 Nacos 拿数据源/Redis/RabbitMQ 配置，
  现已**补全**到 `bootstrap-prod.yaml`（auth / core / multimedia）；
- 补建缺失的 `static/mysql/prod/DDL_auth.sql`（原 prod 环境没有 auth 库脚本）。

### 5. 编排层

- 三个编排文件移除 `magic-nacos` 服务及其 `depends_on` 条目；
- 删除 `k8s/nacos-pv.yaml`、`k8s/nacos-pvc.yaml`。

---

## 影响

### 正面

- **少一个必须常驻的中间件**：本地起服务不再需要等 Nacos ready；
- **依赖树瘦身**：`nacos-client` 及其 6 个传递依赖、`alibabacloud-gateway-spi` 全部消失；
- **启动不再依赖外部注册中心**：Dubbo 不再需要等注册成功才可用；
- **prod 配置不再有「两份」**：此前 prod 配置同时存在于 Nacos 与本地 yaml，
  实际以 Nacos 为准，两者不一致时极难排查；现在只有一份。

### 负面 / 约束

- **失去多实例负载均衡**：一旦某个服务需要多副本，必须重新引入服务发现
  （届时建议直接用 K8s Service / Dubbo 的 `kubernetes://` 注册中心，**不要再把 Nacos 请回来**）；
- **地址变成显式配置**：换环境需要改 bootstrap 文件（但这些文件本来就在改，
  成本并未增加）；
- **prod profile 被补全**：其中的库名（`magic_auth` / `magic_video`）与凭据默认值
  沿用了 local/dev 的约定，首次上 prod 前需按实际环境核对。

---

## 备选方案与否决理由

| 方案 | 否决理由 |
|---|---|
| **保留 Nacos，只修配置** | 保留了一个从未真正工作的配置中心 + 一个单实例下无收益的注册中心；问题只是从「坏」变成「没用」 |
| **上 K8s，用 K8s 原生能力替换 Nacos** | 与「不上 K8s 集群」的既有决策冲突；dev 模式（IDEA 跑服务）与 K8s 原生服务发现互斥；Dubbo 随机端口需改造；现有 manifest 全部损坏需重写 |
| **用 Nacos 只做注册中心，配置全放本地** | 仍保留一个常驻中间件，而它唯一的职责（单实例服务发现）收益最低 |
| **Spring Cloud StaticDiscoveryClient 静态实例列表** | 能保留 `lb://` 写法，但仍需为每个实例硬编码地址，且 Dubbo 侧仍要单独处理；与「直接写 `http://` 地址」相比没有额外收益 |

---

## 验证

无 Nacos 环境下的运行时验证（`dev` profile，`auth` + `core` + `gateway` 三服务）：

| 验证项 | 结果 |
|---|---|
| `auth` 启动 | ✅ `Started AuthApplication in 11.053 seconds` |
| `core` 启动 | ✅ `Started CoreApplication in 13.199 seconds` |
| `gateway` 启动 | ✅ `Started GatewayApplication in 6.826 seconds` |
| 网关 → auth 登录（HTTP 直连路由） | ✅ `{"code":200,"msg":"操作成功"}` |
| 网关 → core 列表（HTTP 直连 + Sa-Token + Dubbo 鉴权） | ✅ `total:41, pages:14` |
| Dubbo 服务导出 | ✅ `Export dubbo service ...UserPermService to url dubbo://...:20880`<br>✅ `Export dubbo service ...ResourceService to url dubbo://...:20881` |
| 依赖树中的 Nacos | ✅ 无（`mvn dependency:tree` 已确认） |

> 注：`multimedia` / `has` 的运行时验证受沙盒限制（MinIO 镜像无法拉取），
> 其改动（MQ 消息契约、落库通道）已由 `TranscodeMessageContractTest`、
> `DeadLetterRoutingTest`、`DubboResourceServiceTest` 三个真实中间件集成测试覆盖。

---

## 关联文档

- [`02-architecture.md`](../02-architecture.md) — 调用矩阵已同步为直连形态
- [`09-tech-debt.md`](../09-tech-debt.md) — P1-10 / P1-11 已关闭
- [`10-architecture-review.md`](../10-architecture-review.md) — A-1 / A-2 的原始问题描述
