# ADR-0009 · 移除 Dubbo，服务间同步改为 HTTP + OpenAPI

- 状态：✅ 已落地
- 日期：2026-11-08
- 关联：技术债 P0-4 / P2-2 / P3-4；[ADR-0008](0008-remove-nacos.md)；设计规格第 3、6、8 节

---

## 背景

ADR-0008 去掉 Nacos 之后，服务间同步仍是 Dubbo URL 直连：

| 调用 | 原接口 | 端口 |
|---|---|---|
| gateway → auth | `UserPermService` 查角色/权限 | 20880 |
| has → core | `ResourceService` 读写播放源 | 20881 |

这对当前单机部署能工作，但和后续 K8s / 服务网格的方向冲突：

1. 契约装在 `common-api` jar 里，改签名就要一起发版，网格侧看不到 HTTP 路径与状态码。
2. Dubbo 默认超时 1s、重试 2 次。`saveResource` 不是幂等写入的安全默认（即使后来按 videoId+type+src 去重）。
3. 协议端口必须固定，和「以后用 Service DNS + HTTP」是两套寻址。
4. 网关每次请求打一次 RPC，失败语义和 HTTP `Result` 不一致。

---

## 决策

**彻底移除 Dubbo。** 同步跨服务只走 HTTP，契约写在 `contracts/openapi/`，不生成客户端代码。

### 1. 路径

| 原 Dubbo | 新 HTTP | 调用方 |
|---|---|---|
| `getPermByUserId` | `GET /api/v1/internal/users/{id}/permissions` | gateway `AuthPermClient`（WebClient） |
| `getRoleByUserId` | `GET /api/v1/internal/users/{id}/roles` | gateway |
| `getPermWithRolesByUserId` | 本轮不暴露 | — |
| `saveResource` | `POST /api/v1/internal/resources` | has `CoreResourceClient`（RestClient） |
| `getResourceById` | `GET /api/v1/internal/resources/{id}` | has（需要时） |

对外路径不变：客户端走 Gateway 的 `/auth/**`、`/core/**`、`/multimedia/**`。`/api/v1/internal/**` **没有**网关路由，local/prod 不把 auth/core 端口映射到宿主机。

### 2. 寻址与超时

显式 base URL，不引入注册中心：

| 配置 | local / prod | dev |
|---|---|---|
| `auth.base-url` | `http://magic-auth:8999` | `http://localhost:8999` |
| `core.base-url` | `http://magic-core:9001` | 文件中仍是 `http://magic-core:9001` |

客户端超时写死在代码里：gateway 连接/读取 3s；has 连接 3s、读取 30s。`POST /resources` 不自动重试。

### 3. 错误

| 场景 | 行为 |
|---|---|
| gateway → auth 超时或 5xx | 抛出不可用，**不**用空权限放行 |
| gateway → auth 4xx | 按无角色/无权限（空列表） |
| has → core 非成功 | 抛出，消费端 nack，不把「对象已在 MinIO、库无记录」当成成功 |

响应信封仍是 `Result`（`code=200`、`msg=操作成功`）。`GET` 播放源**不回填 `md5`**：`video_source` 没有这一列，示例里该字段为 null。

### 4. 依赖

根 POM 与五个服务不再依赖 `dubbo-spring-boot-starter` 或 `common-api`。协议端口 20880–20883 删除。

---

## 影响

- 服务间调用可以用普通 HTTP 探针、网格超时和 mTLS，不必再维护 Dubbo 端口。
- 契约以 YAML 审阅，实现侧手写薄 DTO。
- 网关仍是每次请求访问 auth，进程内短缓存还没做（技术债 P2-2 保持开放，只是传输从 Dubbo 换成 HTTP）。
- dev 下 has 的 `core.base-url` 与同文件里的 localhost 中间件不一致。宿主机跑 has 时，`magic-core` 必须能解析，或把该值改成 `http://localhost:9001`。

---

## 备选方案与否决理由

| 方案 | 否决理由 |
|---|---|
| 保留 Dubbo，只加超时和版本号 | 仍是二进制 RPC，网格和 K8s Service 帮不上忙 |
| 改成 gRPC | 设计规格列为 Phase 2 可选项，本轮要先去掉 Dubbo |
| Spring Cloud OpenFeign | 多一层声明式客户端，契约源仍然要另放；手写 WebClient/RestClient 更薄 |
| openapi-generator 生成客户端 | 本轮 diff 和生成物维护成本高于手写 DTO |

---

## 本轮非目标（Phase 2，只写文档）

1. 可运行的 Deployment / Service / Ingress 或 Gateway API
2. ConfigMap / Secret 外置；**不**把 Nacos 请回来
3. 服务网格 mTLS 替代网络隔离；超时、重试、可观测外置到网格
4. 评估用 Ingress 换掉 Spring Cloud Gateway
5. 内部调用升级 gRPC；Flyway；多副本走 K8s DNS

本轮信任边界是 compose 网络：internal 端口不映射到宿主机。可选的 `X-Internal-Token` 未强制。

---

## 验证

| 项 | 结果 |
|---|---|
| `rg -i dubbo`（`*.java`、`*.xml`、`*.yaml`、`pom.xml`） | 无匹配。README 里出现该词，只用来说明协议已删除 |
| gateway / has / auth / core 单测 | 既有 `AuthPermClientTest`、`CoreResourceClientTest`、internal Controller 测试 |
| compose 主链路烟测 | **未跑**。宿主机没有 `java` / `mvn`，镜像 Dockerfile 只拷贝 `target/*.jar`，仓库里没有这些 jar |

---

## 关联文档

- [`0010-platform-and-contracts.md`](0010-platform-and-contracts.md)
- [`0011-single-minio-redis.md`](0011-single-minio-redis.md)
- [`../02-architecture.md`](../02-architecture.md)
- [`../superpowers/specs/2026-11-08-cloud-native-architecture-design.md`](../superpowers/specs/2026-11-08-cloud-native-architecture-design.md)
