# 云原生导向架构优化设计

> 状态：已批准（实现计划已出，暂不 commit）  

> 日期：2026-11-08  
> 方案：协议迁移 + 基础设施收敛（方案 2）  
> 前置审查：仓库现状 / 服务依赖（见会话 Canvas `architecture-review.canvas.tsx`）

## 1. 背景与目标

`magic-video-backend` 是 Java 17 Maven monorepo 下的视频点播后端：5 个 Spring Boot 服务 + `common` 共享库，已移除 Nacos（ADR-0008）。主链路（鉴权 / 元数据 / 上传转码）已基本打通，但存在：

- 同步跨服务仍依赖 **Dubbo 直连**（与云原生 K8s / Mesh 演进方向不一致）
- `common` 偏「全家桶耦合」，尤其 `common-api` 以二进制 jar 充当 RPC 契约
- 双 MinIO、双 Redis、生产编排缺媒体链路、CI 只推 2 个镜像等运维与真相面问题

### 1.1 目标（本轮）

1. 保持 **微服务形态**（5 服务），面向 **K8s / 服务网格** 演进，弱化 Dubbo / Nacos 类栈
2. **彻底移除 Dubbo**；服务间同步改为 **HTTP + OpenAPI**
3. **保留 Spring Cloud Gateway**（路由 + Sa-Token）
4. 将 `common` 收敛为 **`platform-web` + `contracts`**；删除 `common-api`；`common-db` / MQ 实现内联到相关服务
5. 收敛中间件（单 MinIO、单 Redis），对齐 compose / CI / 删减死代码与废弃 k8s

### 1.2 非目标（本轮不做）

- 合并业务服务为单体
- 用 Ingress / Gateway API 替换 Spring Cloud Gateway
- 引入服务网格或 mTLS
- 引入 gRPC
- 交付可运行的 K8s 清单（仅文档化 Phase 2）
- 合并 MySQL 双库
- 强制引入 Flyway / Testcontainers 全套

## 2. 目标架构与服务边界

职责不变，只改连接方式：

```
Client
  │ HTTP
  ▼
Gateway :9000          # 路由 + Sa-Token；鉴权改调 Auth HTTP internal API
  ├─ HTTP → auth
  ├─ HTTP → core
  └─ HTTP → multimedia

multimedia ──RabbitMQ──► has
has ──HTTP internal──► core   # 原 Dubbo ResourceService
```

| 服务 | 本轮变化 |
|---|---|
| gateway | 去掉 Dubbo 客户端；`StpInterface` 经 WebClient/RestClient 调 auth；去掉无用 loadbalancer |
| auth | 新增 internal HTTP（用户角色/权限）；去掉 Dubbo provider |
| core | 新增 internal HTTP（`video_source` 读写）；去掉 Dubbo provider |
| multimedia | 上传 + 发 MQ；MinIO 改为单实例 bucket |
| has | 转码成功后 HTTP 调 core；去掉 Dubbo；MinIO 同单实例 |

**寻址**：继续显式 URL（compose 服务名）。文档约定将来使用 K8s Service DNS，**不**再引入 Nacos。

## 3. HTTP / OpenAPI 契约与鉴权流

### 3.1 原则

- **对外**（经 Gateway）：路径约定不变——`/auth/**`、`/core/**`、`/multimedia/**`
- **对内**（服务→服务）：`/api/v1/internal/**`，**不**注册到 Gateway 公网路由，仅内网可达
- 契约源在 `contracts/`（OpenAPI / AsyncAPI），**不再**用 `common-api` jar 充当 RPC 契约

### 3.2 替换映射

| 原 Dubbo | 新 HTTP | 调用方 |
|---|---|---|
| `UserPermService.getPermByUserId` | `GET /api/v1/internal/users/{id}/permissions` | gateway |
| `UserPermService.getRoleByUserId` | `GET /api/v1/internal/users/{id}/roles` | gateway |
| `UserPermService.getPermWithRolesByUserId` | 本轮若网关未使用则不暴露；需要时用 `GET .../permissions?includeRoles=true` | — |
| `ResourceService.saveResource` | `POST /api/v1/internal/resources` | has |
| `ResourceService.getResourceById` | `GET /api/v1/internal/resources/{id}` | has（若仍需要） |

响应统一 `Result<T>`；资源 body 对齐现有 `ResourceDto` 字段。

### 3.3 鉴权流

1. 客户端 → Gateway；Sa-Token 校验登录态（Redis session）
2. `StpInterface` 改为 HTTP 调 auth internal API
3. 进程内短缓存（如 Caffeine，TTL 数十秒）降低每请求打 auth 的压力
4. 业务请求仍按现路由转发；internal 路径不暴露公网

### 3.4 服务间信任

- compose/单机：依赖网络隔离（不映射 internal 专用端口到宿主机）
- 配置预留可选 `X-Internal-Token` / `internal.shared-secret`
- Phase 2 由 Mesh mTLS 替代；本轮不强制 mTLS

### 3.5 契约产物布局

```
contracts/
  openapi/
    auth-internal.v1.yaml
    core-internal.v1.yaml
  asyncapi/
    transcode.v1.yaml
```

本轮客户端 DTO **优先手写薄类型**（gateway / has），不强制引入 openapi-generator。

## 4. `common` → `platform` / `contracts`

### 4.1 目标目录

```
magic-video-backend/
├── contracts/                 # 契约源（非 Spring 应用）
├── platform/
│   └── platform-web/          # Result、异常、分页、Jackson 等无业务语义
├── auth/ / core/              # MyBatis-Plus 配置内联到有 DB 的服务
├── multimedia/ / has/         # MQ 生产/消费实现分属两端
└── （删除）common/ 整树
```

### 4.2 模块去向

| 现模块 | 处理 |
|---|---|
| `common-api` | **删除** |
| `common-utils` | 迁为 `platform-web`；移出网关专属 CORS 等到 gateway；清理未使用工具类；去掉 parent 上无意义的统一 webflux 依赖 |
| `common-db` | `MybatisPlusConfig` / `MyMetaObjectHandler` **复制**到 auth、core；删除 `ContextUtil` 空壳与 library 内伪 `*Application` |
| `common-message` | schema → `contracts/asyncapi`；`RabbitMQConfig` + Producer → multimedia；Consumer → has；保留契约测试思路 |

### 4.3 依赖规则

1. 服务可依赖 `platform-web`；禁止依赖已删除的 `common-api` 或其他业务服务
2. `platform-web` 禁止依赖业务模块、Dubbo、具体 MQ 业务实体
3. 服务间只通过 HTTP（OpenAPI）或 MQ（AsyncAPI）通信，不共享实体/Mapper
4. monorepo 父 POM / BOM 统一版本——这是仓库组织方式，不是分布式单体

包名：目标 `cn.xiaolin.platform.web...`；若为减小 diff，可暂留 `cn.xiaolin.utils` 并在 ADR 标明后续 rename。

## 5. 基础设施收敛与删减

### 5.1 中间件

| 现状 | 目标 | 做法 |
|---|---|---|
| MinIO ×2 | **×1** | 固定 bucket 名：`raw`、`transcoded` |
| Redis ×2 | **×1** | 前缀或 DB 隔离：`sa-token:` vs `core:` |
| MySQL 双库 | **保留** | 本轮不合并 |
| RabbitMQ | **保留** | 仅 multimedia→has；删除 core 无用 rabbit 配置 |

### 5.2 编排与 CI

- `docker-compose-local.yaml` / `dev`：单 MinIO、单 Redis；配置同步
- `mini-local.yaml`：补齐 multimedia、has、MinIO（关闭技术债 P1-13）
- `.github/workflows/prod.yml`：构建推送 **5** 个服务镜像；镜像命名统一
- `k8s/*.yaml`：删除或移入 `docs/archive/`；本轮不交付可运行 K8s

### 5.3 代码与依赖删减

- 全仓 Dubbo 依赖、端口、provider/consumer
- gateway `spring-cloud-starter-loadbalancer`
- gateway 上 `/has/**` 鉴权且无路由的死配置
- `NotImplemented` 空壳端点：优先删除未承诺 API；与 internal 重复的对外 Resource CRUD 删除或标明仅内部
- `volume/promtail/`（Alloy 已替代）
- 过时模块 README TODO 区块按新架构改写

### 5.4 DDL

- 以 local/prod 为真源，使 dev/local/prod **表集合一致**
- 无法映射到现模型的历史表归档说明
- Flyway 列为 Phase 2，本轮不做强制

## 6. 错误处理

| 场景 | 行为 |
|---|---|
| Gateway → auth 超时/5xx | 依赖不可用 → 明确 503（或项目统一错误码）；**禁止**空权限放行 |
| Gateway → auth 4xx | 按无权限处理 |
| has → core 落库失败 | 与现有 MQ ack/DLQ 约定对齐：不可静默成功；避免「对象在 MinIO、库无记录」 |
| 业务与 internal API | 统一 `Result` + 全局异常处理 |
| Internal token 校验失败 | 401/403，不泄漏内部细节 |

客户端：**显式 connect/read timeout**。幂等 GET（权限查询）可有限重试；`POST /resources` 默认不重试（除非后续定义幂等键）。

## 7. 测试与完成定义

### 7.1 测试

1. OpenAPI 与 Controller 字段对齐；MQ 契约/DLQ 测试迁移保留
2. 单测：gateway 权限客户端（MockWebServer/WireMock）；has→core 客户端；auth/core internal Controller
3. 集成最低标准：compose 烟测——注册登录 → 上传 → 转码 → 可查 `video_source`
4. 回归：`rg -i dubbo` 为零；compose 仅 1 Redis + 1 MinIO

### 7.2 完成定义（DoD）

1. 全栈 compose 主链路跑通
2. 鉴权不依赖 Dubbo
3. CI 产出 5 镜像；`mini-local` 含媒体链路
4. 仓库无 Dubbo 依赖残留
5. `common/` 已移除或等价替换为 `platform` + `contracts`，依赖规则生效
6. 架构文档与 ADR 已更新

## 8. Phase 2（仅文档，本轮不实现）

1. 可运行 K8s：Deployment / Service / Ingress 或 Gateway API
2. ConfigMap / Secret 外置配置；仍不回 Nacos
3. 服务网格：mTLS 替代 internal token；超时/重试/可观测外置
4. 评估 Ingress/Gateway API 替换 Spring Cloud Gateway；Sa-Token 外置或 BFF
5. 可选：内部调用升 gRPC；Flyway；多副本用 K8s DNS/Endpoints

## 9. 文档与 ADR 交付

本轮实现阶段应同步：

- 更新 `docs/02-architecture.md`、`docs/09-tech-debt.md`（关闭本轮项）
- 新 ADR（建议编号接续）：
  - 移除 Dubbo，改 HTTP + OpenAPI
  - `common` → `platform` / `contracts`
  - 单 MinIO / 单 Redis 收敛
- README 端口、依赖图与 compose 一致

## 10. 已采纳决策摘要

| 决策点 | 选择 |
|---|---|
| 优化目标 | 微服务形态 + 云原生演进 |
| 整体方案 | 方案 2：协议迁移 + 基础设施收敛 |
| Dubbo | 本轮彻底移除 |
| 替代协议 | HTTP + OpenAPI（方案 C） |
| Gateway | 本轮保留 Spring Cloud Gateway |
| common | 折中：platform-web + contracts；删 api；db/message 内联拆分 |

## 11. 风险与缓解

| 风险 | 缓解 |
|---|---|
| Gateway 每请求打 auth 延迟上升 | 短缓存；超时显式；监控 auth 延迟 |
| MinIO/Redis 合并导致配置遗漏 | 改 compose 后强制烟测；检查所有 bootstrap profile |
| common 拆分 diff 过大 | 分步 PR：先 platform/contracts 与去 Dubbo，再删 common 残留 |
| internal API 误暴露 | 不配 Gateway 路由；文档 + 可选 token；评审路由表 |
| DDL 对齐破坏 dev 数据 | 先 diff 三份 DDL，归档删除并注明 |

---

**审阅请确认**：以上设计是否可进入实现计划（writing-plans）？若需修改，请直接指出章节与变更点。
