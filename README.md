# magic-video-backend

> 基于 Spring Cloud 的**视频点播**微服务后端 —— 影视信息管理 + 媒体上传 + 自适应流转码。

个人项目，目标是完整跑通「微服务拆分 → 鉴权 → 媒体存储 → 异步转码 → 流媒体分发」这条链路，
同时作为 Spring Cloud / DevOps / 可观测性的综合练习场。

---

## 目录

- [技术栈](#技术栈)
- [系统架构](#系统架构)
- [模块结构](#模块结构)
- [快速开始](#快速开始)
- [端口与中间件](#端口与中间件)
- [API 约定](#api-约定)
- [数据模型](#数据模型)
- [配置管理](#配置管理)
- [可观测性](#可观测性)
- [开发指南](#开发指南)
- [文档索引](#文档索引)
- [已知问题](#已知问题)
- [项目现状说明](#项目现状说明)

---

## 技术栈

| 分类 | 组件 | 版本 |
|---|---|---|
| 语言 / 运行时 | Java | 17 |
| 框架 | Spring Boot | 3.3.13 |
| 微服务 | Spring Cloud | 2023.0.5（仅用 Gateway，不含服务发现） |
| 网关 | Spring Cloud Gateway | 4.1.5 |
| 服务间同步 | HTTP + OpenAPI | internal `/api/v1/internal/**`，无 Dubbo / Nacos |
| ORM | MyBatis-Plus | 3.5.7（`mybatis-plus-spring-boot3-starter`） |
| 数据库 | MySQL | 8.4（驱动 8.4.0 / 镜像 8.4.4） |
| 缓存 | Redis（Lettuce 连接池） | 由 Boot BOM 管理 |
| 消息队列 | RabbitMQ | 由 Boot BOM 管理 |
| 对象存储 | MinIO SDK | 8.6.0 |
| 鉴权 | Sa-Token | 1.38.0 |
| 转码 | ffmpeg（apt 安装） | 容器内系统包 |
| 文档 | springdoc-openapi (Swagger UI) | 2.x |
| 可观测性 | Prometheus + Grafana + Loki + Alloy | 见 `volume/` |

> ℹ️ **没有注册中心、配置中心，也没有 Dubbo**：单机单实例下 Nacos 收益为零，已移除
> （[ADR-0008](docs/adr/0008-remove-nacos.md)）。服务间同步改为 HTTP
> （[ADR-0009](docs/adr/0009-remove-dubbo-http-openapi.md)）。地址写在各服务 `bootstrap-{profile}.yaml`。

> ⚠️ Boot 目前停在 **3.3.13**：该分支已停止 OSS 漏洞修复，且 `spring-boot-starter-actuator`
> 存在未修复的 CVE-2026-22733。迁移路径见 [`docs/10-architecture-review.md`](docs/10-architecture-review.md) 的「附录 A · 依赖版本升级路线」。

---

## 系统架构

```mermaid
flowchart TB
    Client[客户端 Web / Mobile]

    subgraph Edge["接入层"]
        GW["Gateway :9000<br/>Spring Cloud Gateway (WebFlux)<br/>Sa-Token 鉴权"]
    end

    subgraph Services["业务微服务"]
        Auth["auth :8999<br/>用户 / 角色 / 权限"]
        Core["core :9001<br/>影视信息 CRUD"]
        MM["multimedia :9002<br/>媒体上传 → MinIO"]
        Has["has :9003<br/>ffmpeg 转 HLS / DASH"]
    end

    subgraph Infra["基础设施"]
        MySQL[(MySQL<br/>magic_video / auth)]
        Redis[(Redis<br/>业务缓存 + Sa-Token)]
        MQ[(RabbitMQ<br/>转码消息)]
        MinIO[(MinIO<br/>对象存储)]
    end

    Client -->|HTTP| GW
    GW -->|http 直连 :8999| Auth
    GW -->|http 直连 :9001| Core
    GW -->|http 直连 :9002| MM
    GW -->|HTTP internal 权限| Auth

    MM -->|Producer| MQ
    MQ -->|Consumer| Has
    Has -->|HTTP internal 落库| Core

    Core --> MySQL
    Core --> Redis
    Auth --> MySQL
    Auth --> Redis
    MM --> MinIO
    Has --> MinIO
```

**关键设计点**

- **网关是唯一对外入口**：登录态在网关用 Sa-Token 拦截。角色和权限经 HTTP 问 auth（`auth.base-url`），目前没有短缓存（技术债 P2-2）。
- **没有注册中心，也没有 RPC 框架**：路由和 internal 调用都是显式 HTTP URL（[ADR-0008](docs/adr/0008-remove-nacos.md)、[ADR-0009](docs/adr/0009-remove-dubbo-http-openapi.md)）。
- **异步链路走 RabbitMQ**：上传成功后由 `has` 转码，再 `POST /api/v1/internal/resources` 写入 `video_source`。
- **`has` 不对公网提供业务接口**：它消费 MQ，并调用 core 的 internal API。HTTP 端口只给健康检查。
- **共享代码**：`platform/platform-web` 放 `Result` 与分页；`contracts/` 放 OpenAPI / AsyncAPI。没有 `common/`。

更详细的调用矩阵与部署拓扑见 [`docs/02-architecture.md`](docs/02-architecture.md)。

---

## 模块结构

```
magic-video-backend/
├── gateway/          # 网关：路由转发 + Sa-Token（WebFlux），HTTP 向 auth 要权限
├── auth/             # 用户 / 角色 / 权限，以及 internal 权限查询
├── core/             # 影视信息 CRUD，以及 internal 播放源读写
├── multimedia/       # 图片/视频上传 → MinIO，发送转码消息
├── has/              # 消费 MQ，ffmpeg 编码 HLS / DASH，HTTP 回写 core
├── platform/
│   └── platform-web/ # Result、异常、分页、Jackson（无业务语义）
├── contracts/        # OpenAPI / AsyncAPI 契约源（不进 Maven 反应堆）
├── static/
│   └── mysql/        # 各环境 DDL（dev / local / prod）
├── volume/           # 中间件与可观测性组件的持久化数据/配置
├── docs/             # 设计文档与 ADR（见「文档索引」）
├── docker-compose-dev.yaml    # 中间件（开发：宿主机跑微服务）
├── docker-compose-local.yaml  # 全量容器（本地：一键体验）
├── mini-local.yaml            # 生产编排
└── mini-observability.yaml    # 可观测性编排
```

### 服务职责

| 服务 | 端口 | 职责 | 对外暴露 |
|---|---|---|---|
| `gateway` | 9000 | 统一入口、路由转发、登录校验 | ✅ |
| `auth` | 8999 | 注册/登录/登出、RBAC 权限模型、踢人下线 | ✅（经网关） |
| `core` | 9001 | 影视、合集、季、演员、导演、编剧、制片人、评分等 CRUD | ✅（经网关） |
| `multimedia` | 9002 | 图片/视频上传、分片合并、写入 MinIO | ✅（经网关） |
| `has` | 9003 | 消费转码任务，ffmpeg 编码为 HLS / DASH | ❌（仅内网） |

---

## 快速开始

### 前置要求

- JDK 17+
- Maven 3.8+
- Docker + Docker Compose
- **端口占用检查**：`9000/8999/9001/9002/9003/3306/6379/5672/15672/9010/9011`

---

### 方式一：本地体验（全容器化）

> 适合快速体验，一条命令拉起微服务 + 全部中间件。

**1. 配置环境变量**

仓库根目录已有 `.local.env`。它只需要指定宿主机上的**数据卷路径**与 **DDL 目录**，
服务地址已内置在 `bootstrap-local.yaml`（容器网络内的 `magic-*` 主机名）：

```bash
MYSQL_INIT=/绝对路径/magic-video-backend/static/mysql/local
MINIO_DATA=/绝对路径/magic-video-backend/volume/minio/data
```

**2. 启动**

```bash
docker compose -f docker-compose-local.yaml --env-file .local.env up -d
```

**3. 验证**

```bash
curl http://localhost:9000/actuator/health
```

**4. 停止**

```bash
docker compose -f docker-compose-local.yaml --env-file .local.env down
```

> ⚠️ DockerHub 上的镜像仅提供 **amd64** 架构。ARM 机器（如 Apple Silicon）需要本地构建：
>
> ```bash
> mvn clean package -Dmaven.test.skip=true
> docker build -t xxl1997/magic-auth:0.0.1-SNAPSHOT auth/.
> docker build -t xxl1997/magic-gateway:0.0.1-SNAPSHOT gateway/.
> docker build -t xxl1997/magic-core:0.0.1-SNAPSHOT core/.
> docker build -t xxl1997/magic-multimedia:0.0.1-SNAPSHOT multimedia/.
> docker build -t xxl1997/magic-has:0.0.1-SNAPSHOT has/.
> ```

---

### 方式二：开发调试（推荐）

> 适合日常改代码：中间件跑容器，微服务在 IDE 里跑，改完热重启。

**1. 只启动中间件**

```bash
docker compose -f docker-compose-dev.yaml --env-file .dev.env up -d
```

`.dev.env` 中 `MYSQL_INIT` 指向 `static/mysql/dev`，会自动建表并灌入初始数据。

**2. 在 IDE 中启动微服务**

为每个服务设置启动参数 `--spring.profiles.active=dev`：

![ActiveProfile](assets/idea_active_profile.png)

**3. 按依赖顺序启动**

```
auth  →  gateway  →  core  →  multimedia  →  has
```

- `auth` 先于需要登录的流量：网关用 `auth.base-url` 查权限。dev 是 `http://localhost:8999`，local/prod 是 `http://magic-auth:8999`
- `core` 先于转码落库：has 使用 `core.base-url`。local/prod 为 `http://magic-core:9001`。**dev 的 has 配置目前也是 `http://magic-core:9001`**，在宿主机上跑之前要让这个主机名指向本机，或改成 `http://localhost:9001`
- `has` 可以晚于 multimedia：它只消费队列，转码完成后再调 core

**4. 停止**

```bash
docker compose -f docker-compose-dev.yaml --env-file .dev.env down
```

---

### 方式三：生产部署

> 生产服务与可观测性服务分离部署。

```bash
# 1. 创建网络（首次）
docker network create magic-backend-network

# 2. 启动业务服务
docker compose -p prod -f mini-local.yaml up -d

# 3. 启动可观测性服务
docker compose -p observability -f mini-observability.yaml up -d
```

停止时按相反顺序：

```bash
docker compose -p observability -f mini-observability.yaml down
docker compose -p prod -f mini-local.yaml down
```

`mini-local.yaml` 包含五个业务服务、一个 Redis（`magic-redis`）和一个业务 MinIO（`magic-minio`）。对外端口是 Nginx `8081` 与 MinIO `9010/9011`。见 [ADR-0011](docs/adr/0011-single-minio-redis.md)。

---

## 端口与中间件

### 微服务端口（容器内）

| 服务 | HTTP 端口 | local 对外暴露 | dev 对外暴露 |
|---|---|---|---|
| gateway | 9000 | ✅ 9000 | ✅ 9000（IDE） |
| auth | 8999 | ❌ 仅容器内网 | ✅ 8999（IDE） |
| core | 9001 | ❌ 仅容器内网 | ✅ 9001（IDE） |
| multimedia | 9002 | ❌ 仅容器内网 | ✅ 9002（IDE） |
| has | 9003 | ❌ 仅容器内网 | ✅ 9003（IDE） |

没有 Dubbo 端口。服务间同步走上面的 HTTP 端口，internal 路径不进网关路由。

### 中间件端口

| 组件 | 容器端口 | dev 宿主机端口 | local 宿主机端口 | 用途 |
|---|---|---|---|---|
| mysql | 3306 | 3306 | ❌ | auth 库 + 视频库 |
| magic-redis | 6379 | 6379 | ❌ | Sa-Token（前缀 `sa-token:`）与 core 缓存（前缀 `core:`），database 0 |
| rabbitmq | 5672 / 15672 | 5672 / 15672 | ❌ | 转码消息队列 |
| magic-minio | 9000 / 9001 | 9010 / 9011 | ✅ 9010 / 9011 | bucket `raw`、`transcoded` |
| minio (loki，仅 dev compose) | 9000 / 9001 | 9030 / 9031 | — | 日志存储，不存放媒资 |
| grafana | 3000 | 19000 | ✅ 19000 | 监控大盘 |
| prometheus | 9090 | 9090 | ❌ | 指标采集 |
| nginx | 8081 | — | — | 生产入口（`mini-local.yaml`） |

业务侧只有一个 Redis、一个 MinIO。没有 `magic-auth-redis`，也没有宿主机端口 6389。见 [ADR-0011](docs/adr/0011-single-minio-redis.md)。

> **dev** 模式把中间件端口全部暴露到宿主机，方便 IDEA 直连调试；
> **local** 模式只暴露网关与 MinIO，其余服务只走容器内网；
> **prod** 模式（`mini-local.yaml`）只暴露 Nginx `8081`，作为唯一对外入口。

---

## API 约定

### 路由前缀

**客户端请求路径不带版本前缀**，网关在转发时补上 `/api/v1`：

```
POST /auth/login        → gateway → http://magic-auth:8999/api/v1/auth/login
GET  /core/video/list   → gateway → http://magic-core:9001/api/v1/core/video/list
POST /multimedia/video  → gateway → http://magic-multimedia:9002/api/v1/multimedia/video
```

对应网关配置（`bootstrap-{profile}.yaml`）：

```yaml
- id: auth
  uri: http://magic-auth:8999      # local；dev 为 http://localhost:8999
  filters:
    - PrefixPath=/api/v1           # 网关转发时补版本前缀
  predicates:
    - Path=/auth/**                # 客户端路径，不含 /api/v1
```

> ⚠️ 客户端**不要**自己拼 `/api/v1`：写成 `/api/v1/auth/login` 不会命中 `Path=/auth/**` 路由，
> 会被网关的全局鉴权拦截并返回 `{"code":401,"msg":"当前会话未登陆"}`。

### 统一响应体

所有 HTTP 接口返回 `Result<T>`：

```json
{
  "code": 200,
  "msg": "操作成功",
  "data": { }
}
```

> `msg` 的默认文案是 `操作成功`（见 `platform/platform-web/.../enums/ResponseCode.java`），
> **不是** `success`。分页接口的 `data` 为 `PageResult<T>`：

```json
{
  "code": 200,
  "msg": "操作成功",
  "data": {
    "current": 1,
    "size": 20,
    "total": 41,
    "pages": 3,
    "records": [ ]
  }
}
```

### 鉴权

- 登录成功后客户端持有 `sa-token` Cookie（token 名 `sa-token`，有效期 30 天）。
- 网关统一拦截：未登录返回体 `code=401`，无角色/权限返回体 `code=403`。

  > ⚠️ **HTTP 状态码始终是 200**，错误在响应体的 `code` 字段里。
  > 客户端必须判断 `body.code`，不能只判断 `response.status`。实测：
  > `GET /core/video/list` 未登录时返回 HTTP 200 + `{"code":401,"msg":"当前会话未登陆"}`。
- **免登录白名单**（见 `gateway/.../config/SaTokenConfig.java`）：

  ```
  /auth/login  /auth/register
  /swagger-ui/**  /swagger-resources/**  /v3/api-docs/**  /webjars/**  /doc.html
  /actuator/health  /actuator/health/**  /actuator/info
  ```

> `/auth/logout` 与 `/auth/kick-out` **不在**白名单内 —— 它们需要有效会话，且需要校验调用方是否有权强制他人下线。
> 详见 [ADR-0004](docs/adr/0004-sa-token-public-route.md)。

### 主要接口

> 下表路径是**网关转发后到达后端服务**的路径（含 `/api/v1`）。
> 客户端调用时**去掉 `/api/v1` 前缀** —— 例如客户端请求 `POST /auth/login`，
> 网关转发为 `POST /api/v1/auth/login` 给 auth 服务。

<details>
<summary><b>auth</b> —— 认证与 RBAC</summary>

| 方法 | 路径 | 说明 |
|---|---|---|
| POST | `/api/v1/auth/register` | 注册（免登录） |
| POST | `/api/v1/auth/login` | 登录（免登录） |
| POST | `/api/v1/auth/logout` | 登出 |
| POST | `/api/v1/auth/kick-out` | 踢人下线（管理员） |
| GET/POST/PUT/DELETE | `/api/v1/auth/user[/{id}]` | 用户管理 |
| GET | `/api/v1/auth/user/having-perm/{id}` | 拥有某权限的用户 |
| GET | `/api/v1/auth/user/having-role/{id}` | 拥有某角色的用户 |
| GET/POST/PUT/DELETE | `/api/v1/auth/perm[/{id}]` | 权限管理 |
| GET/POST/PUT/DELETE | `/api/v1/auth/role[/{id}]` | 角色管理 |
| GET/POST/PUT/DELETE | `/api/v1/auth/user-perm[/{id}]` | 用户-权限关联 |
| GET/POST/PUT/DELETE | `/api/v1/auth/user-role[/{id}]` | 用户-角色关联 |
| GET/POST/PUT/DELETE | `/api/v1/auth/role-perm[/{id}]` | 角色-权限关联 |

</details>

<details>
<summary><b>core</b> —— 影视信息</summary>

| 方法 | 路径 | 说明 |
|---|---|---|
| GET | `/api/v1/core/video/list` | 分页查询视频（`current`/`size`/`sortBy`/`direction`） |
| GET | `/api/v1/core/video/{id}` | 视频详情（走 Redis 缓存，TTL 10 分钟） |
| GET | `/api/v1/core/video/all` | 全量视频（**已废弃**，见 ADR-0002） |
| GET | `/api/v1/core/video/slide` | 首页轮播 |
| GET | `/api/v1/core/resource/all` | 资源分页列表 |
| GET | `/api/v1/core/resource/recommend` `/banner` `/latest` `/classic` `/featured` | 资源推荐位（**当前 5 个端点逻辑完全相同**，见 P1-7） |
| GET/POST/PUT/DELETE | `/api/v1/core/{category,tag,district,award}[/{id}]` | 各维度 CRUD |
| GET/POST/PUT/DELETE | `/api/v1/core/{video-actor,video-director,video-scripter,video-maker,video-category,video-district,video-tag}[/{id}]` | 影视人员/关联关系 CRUD |
| GET | `/api/v1/core/season/list` `/season/{id}` | 季（**只读**） |
| GET | `/api/v1/core/collection/list` `/collection/set` | 合集（**只读**） |

</details>

<details>
<summary><b>multimedia</b> —— 媒体上传</summary>

| 方法 | 路径 | 说明 |
|---|---|---|
| POST | `/api/v1/multimedia/image` | 图片上传（MultipartFile） |
| POST | `/api/v1/multimedia/video` | 整文件视频上传 |
| POST | `/api/v1/multimedia/video/chunk` | 分片上传 |
| POST | `/api/v1/multimedia/video/merge` | 分片合并 |

</details>

### 内部 HTTP（不对网关暴露）

契约在 `contracts/openapi/`。响应仍是 `Result`。

| 方法 | 路径 | 提供方 | 调用方 |
|---|---|---|---|
| GET | `/api/v1/internal/users/{id}/permissions` | auth | gateway |
| GET | `/api/v1/internal/users/{id}/roles` | auth | gateway |
| POST | `/api/v1/internal/resources` | core | has |
| GET | `/api/v1/internal/resources/{id}` | core | has（需要读回时） |

`GET` 播放源的 `md5` 为 null：`video_source` 不存这个字段。

---

## 数据模型

两个业务库：**auth 库**（权限）与 **magic_video 库**（影视业务），同一 MySQL 实例。

### auth 库

| 表 | 说明 |
|---|---|
| `sys_user` | 用户 |
| `sys_role` | 角色 |
| `sys_permission` | 权限 |
| `sys_user_role` | 用户-角色关联 |
| `sys_user_permission` | 用户-权限关联（直接授权） |
| `sys_role_permission` | 角色-权限关联 |

### magic_video 库

| 表 | 说明 |
|---|---|
| `video` | 影视主表 |
| `video_source` | 资源表（播放源、清晰度、码率） |
| `collection` | 合集 |
| `season` | 季 |
| `category` / `video_category` | 分类及其关联 |
| `district` / `video_district` | 地区及其关联 |
| `tag` / `video_tag` | 标签及其关联 |
| `video_actor` | 演员 |
| `video_director` | 导演 |
| `video_scripter` | 编剧 |
| `video_maker` | 制片人 |
| `award` | 获奖信息 |

DDL 脚本按环境分目录存放：

```
static/mysql/
├── dev/     DDL_auth.sql (6 张)  DDL_video.sql (15 张)
├── local/   DDL_auth.sql (6 张)  DDL_video.sql (15 张)
├── prod/    DDL_auth.sql (6 张)  DDL_video.sql (15 张)
└── archive/ 旧草稿（含已废弃的 videoResource，不要挂到 MYSQL_INIT）
```

三套环境的表集合一致。库名和种子数据仍按环境区分。Flyway 还没有引入。

### 主键策略

全部 entity 使用 **MyBatis-Plus 内置雪花算法**（`@TableId(type = IdType.ASSIGN_ID)`），
不依赖数据库自增，也不引入外部 ID 协调服务。详见 [ADR-0001](docs/adr/0001-snowflake-id.md)。

---

## 配置管理

### Profile 机制

每个服务有 `bootstrap.yaml` + `bootstrap-{local,dev,prod}.yaml`，通过
`--spring.profiles.active={profile}` 切换。

| profile | 场景 | 依赖地址 | 覆盖的服务 |
|---|---|---|---|
| `local` | 全部容器化 | `magic-mysql` / `magic-redis` / `magic-rabbitmq`（容器主机名） | 全部 5 个 |
| `dev` | 中间件容器 + 微服务宿主机 | `localhost` | 全部 5 个 |
| `prod` | 生产编排 | `magic-mysql` / `magic-redis` / `magic-rabbitmq` | 全部 5 个 |

### 没有配置中心：配置随 jar 走

**本项目没有配置中心，也没有注册中心**。Nacos 已于 2026-11-08 彻底移除
（见 [ADR-0008](docs/adr/0008-remove-nacos.md)）。理由很直接：**单机单实例部署下，
服务发现与配置中心的收益为零，却各多了一个必须常驻的中间件**。

现在地址有两种写法：

**1. Spring Cloud Gateway 路由 → HTTP 直连**

```yaml
# gateway/.../bootstrap-local.yaml
spring:
  cloud:
    gateway:
      routes:
        - id: auth
          uri: http://magic-auth:8999      # dev 下为 http://localhost:8999
          filters:
            - PrefixPath=/api/v1
          predicates:
            - Path=/auth/**
```

**2. 服务间 HTTP → base URL**

```yaml
# gateway/.../bootstrap-local.yaml
auth:
  base-url: http://magic-auth:8999    # dev 为 http://localhost:8999

# has/.../bootstrap-local.yaml
core:
  base-url: http://magic-core:9001
```

internal 路径不写进网关路由。契约源是 `contracts/openapi/`，不是 jar。

### 密钥处理

敏感项已通过环境变量占位符注入，**默认值仅供本地开发**：

```yaml
username: ${MYSQL_USERNAME:root}
password: ${MYSQL_PASSWORD:123456}          # ← 生产必须覆盖
rabbitmq:
  username: ${RABBITMQ_DEFAULT_USER:xiaolin}
  password: ${RABBITMQ_DEFAULT_PASS:123456rabbit}
```

> 生产环境**不要**依赖默认值。同时仓库已开启 `secret_scanning` 与
> `secret_scanning_push_protection`（历史上曾误提交 `.env.local`，目前仅含占位符）。

---

## 可观测性

| 组件 | 作用 | 位置 |
|---|---|---|
| Prometheus | 指标采集（Actuator 暴露） | `volume/prometheus/` |
| Grafana | 监控大盘 | `volume/grafana/` |
| Loki + Alloy | 日志聚合与采集 | `volume/loki/`、`volume/alloy/` |

各服务的 Actuator 端点已收敛为 `health,info`，并排除了 `cloudfoundry` 与 `gateway`：

```yaml
management:
  endpoints:
    web:
      exposure:
        include: health,info
        exclude: cloudfoundry,gateway
```

---

## 开发指南

### 构建

```bash
# 全量构建（跳过测试）
mvn clean install -DskipTests

# 单模块
mvn -pl core -am clean package
```

### 分页接口规范

列表接口统一使用 `PageQuery` / `PageResult`（`platform-web`，不依赖 MyBatis-Plus）：

```java
@GetMapping("/video/list")
public Result<PageResult<VideoVO>> list(PageQuery pageQuery) { ... }
```

| 参数 | 默认值 | 约束 |
|---|---|---|
| `current` | 1 | ≥ 1 |
| `size` | 20 | 1 ~ 200（`PageConsts`） |
| `sortBy` | 创建时间倒序 | 支持 `rating` / `duration` / `release` / `latest` |
| `direction` | `desc` | `asc` / `desc` |

旧的 `getVideoList()` / `list()` 等无分页方法保留但已标注 `@Deprecated`，
存量调用方（如 `CollectionServiceImpl`）暂未迁移。

### 缓存规范

`core` 模块启用 Spring Cache（Redis 实现）：

- Key 前缀 `core:`，TTL 10 分钟
- **不缓存 null**（`disableCachingNullValues`）
- **不要用 `Optional` 作为缓存值** —— 序列化器会拆包，读回类型不匹配
- 只缓存按 id 的单条查询；**不要缓存 List**（无界集合 = OOM 风险）

### 对象存储规范

MinIO 为单实例 `magic-minio`，两个 bucket：`raw`（multimedia 写入图片与原片）和 `transcoded`（has 写入 HLS/DASH）。has 通过转码消息里的预签名 `sourceUrl` 从 `raw` 拉取原片。

对象 key **绝不使用用户上传的原始文件名**。图片为 `images/<UUID>.<安全后缀>`，视频为 `videos/<UUID>.<安全后缀>`；后缀从白名单取，取不到时回退到业务类型自带的固定后缀。详见 [ADR-0005](docs/adr/0005-minio-object-key.md)。

转码产物在 bucket `transcoded` 内按前缀分开：`hls/<resourceKey>/<resourceKey>.m3u8` 与 `dash/<resourceKey>/<resourceKey>.mpd`。`resourceKey` 是 32 位 hex，由 `multimedia` 生成并**随 MQ 消息一起传递**（`TranscodeMessage.resourceKey`），两侧不各写一套 URL 解析规则。

---

## 文档索引

设计文档（`docs/`）与使用文档（本 README）职责分离：

| 文档 | 内容 |
|---|---|
| [`docs/02-architecture.md`](docs/02-architecture.md) | 服务清单、调用矩阵、部署拓扑、明确边界 |
| [`docs/09-tech-debt.md`](docs/09-tech-debt.md) | 技术债清单（P0/P1/P2/P3 分级 + 进度跟踪） |
| [`docs/10-architecture-review.md`](docs/10-architecture-review.md) | **架构审查报告与设计优化建议** |
| [`docs/adr/0001-snowflake-id.md`](docs/adr/0001-snowflake-id.md) | 主键生成策略：雪花算法 ✅ |
| [`docs/adr/0002-pagination.md`](docs/adr/0002-pagination.md) | 列表接口统一分页 ✅ |
| [`docs/adr/0003-cache-strategy.md`](docs/adr/0003-cache-strategy.md) | Spring Cache 接入与配置 ✅ |
| [`docs/adr/0004-sa-token-public-route.md`](docs/adr/0004-sa-token-public-route.md) | Sa-Token 白名单收紧 ✅ |
| [`docs/adr/0005-minio-object-key.md`](docs/adr/0005-minio-object-key.md) | MinIO 对象 key UUID 化 ✅ |
| [`docs/adr/0006-async-transcoding.md`](docs/adr/0006-async-transcoding.md) | HAS 转码异步化（MQ 队列有界并发 + ffmpeg 超时）✅ |
| [`docs/adr/0007-rabbitmq-consumer-ack.md`](docs/adr/0007-rabbitmq-consumer-ack.md) | RabbitMQ 手动 ack + 死信队列 ✅ |
| [`docs/adr/0008-remove-nacos.md`](docs/adr/0008-remove-nacos.md) | 移除 Nacos：直连 + 本地配置 ✅ |
| [`docs/adr/0009-remove-dubbo-http-openapi.md`](docs/adr/0009-remove-dubbo-http-openapi.md) | 移除 Dubbo，内部改为 HTTP + OpenAPI ✅ |
| [`docs/adr/0010-platform-and-contracts.md`](docs/adr/0010-platform-and-contracts.md) | `common` 收敛为 `platform-web` + `contracts` ✅ |
| [`docs/adr/0011-single-minio-redis.md`](docs/adr/0011-single-minio-redis.md) | 业务 Redis / MinIO 各一个实例 ✅ |

各模块还有独立 README：[gateway](gateway/README.md) · [auth](auth/README.md) ·
[core](core/README.md) · [multimedia](multimedia/README.md) · [has](has/README.md) ·
[platform-web](platform/platform-web/README.md) · [contracts](contracts/README.md)

---

## 已知问题

完整清单见 [`docs/09-tech-debt.md`](docs/09-tech-debt.md)，按「今天不修明天会不会出问题」分级。

### 已关闭（2026-11-08）

| 级别 | 问题 | 落地方式 |
|---|---|---|
| P0 | MQ 消息类型与可信包白名单冲突 → 转码链路从未跑通 | 改用 `TranscodeMessage` DTO |
| P0 | auth 自定义 SQL 硬编码 `dev_auth.` → local/prod 登录必挂 | 去前缀 + 补 `deleted = 0` |
| P0 | 播放源无法落库 | HTTP `POST /api/v1/internal/resources`（原 Dubbo 通道已删除） |
| P0 | 合并/上传/发消息被一个 `if` 包住 → 消息永不补发 | 拆三步，只保护合并 |
| P0 | ffmpeg 同步阻塞且占用唯一 MQ 消费线程 | 有界并发 + 超时 + 手动 ack + DLQ |
| P0 | MyBatis-Plus starter 用错变体 → **全项目无法启动** | 换 `mybatis-plus-spring-boot3-starter` |
| P0 | 历史弱口令迁移触发强度校验 → auth 启动失败 | 新增绕开强度校验的迁移入口 |
| P1 | Nacos 配置中心名不副实 + 配置数据有误 | **彻底移除 Nacos**，改直连 + 本地配置 |
| — | Dubbo 与 `common/` | HTTP + OpenAPI；`platform-web` + `contracts`（ADR-0009、0010） |
| P1 | 双业务 Redis / MinIO，生产编排缺媒体服务 | 各保留一个实例；`mini-local` 含 multimedia、has、MinIO（ADR-0011） |
| P1 | DDL 三套表集合不一致 | dev/local/prod 对齐；Flyway 仍未做 |

### 仍需关注

| 级别 | 问题 | 位置 |
|---|---|---|
| P1 | 全模块事务与吞异常仍未整理 → 服务端故障可能被映射成客户端 400 | `*/service/impl/*ServiceImpl.java` |
| P1 | 乐观锁因调用形态错误而失效（`update(null, wrapper)`） | `core`、`auth` 各 ServiceImpl |
| P1 | 缓存无失效策略 → 更新后最长 10 分钟返回旧数据 | `core/.../VideoServiceImpl.java` |
| P1 | Service 层直接返回 Entity，可能暴露 `deleted`/`revision` | `core`、`auth` 各 Controller |
| P1 | 网关每次请求都 HTTP 调 auth 查权限，没有短缓存 | `gateway/.../client/AuthPermClient.java` |
| P1 | 转码进度不可查询、无自动重试（无任务表/状态机） | `has`（见 [ADR-0006](docs/adr/0006-async-transcoding.md)） |
| P2 | MinIO bucket 公共可读，拿到 URL 即可下载原片 | `multimedia/config/AppConfig.java` |
| P2 | 登录接口无失败限速 | `auth/.../SysUserServiceImpl.java` |

---

## 项目现状说明

这是一个**个人练习项目**。服务拆分、鉴权模型、媒体转码链路在代码和编排上已经按 HTTP internal、单 Redis、单业务 MinIO 对齐。
2026-11-08 的 compose 主链路烟测因宿主机没有 JDK、镜像又依赖预先打好的 jar 而没有在本环境重跑。
手工步骤见 [`docs/superpowers/plans/smoke-checklist-cloud-native.md`](docs/superpowers/plans/smoke-checklist-cloud-native.md)。

仍未完成的部分见「已知问题」。它不是开箱即用的生产级产品，也不打算追求生产级完备性 ——
目标是完整走通技术栈并沉淀设计决策。
