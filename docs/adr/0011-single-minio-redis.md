# ADR-0011 · 业务 Redis 与 MinIO 各保留一个实例

- 状态：✅ 已落地
- 日期：2026-11-08
- 关联：技术债 P1-13；设计规格第 5 节

---

## 背景

收敛前，compose 里有两套缓存和两套对象存储：

| 组件 | 旧形态 | 问题 |
|---|---|---|
| Redis | `magic-redis`（业务，宿主机 6389）+ `magic-auth-redis`（Sa-Token，6379） | 单机两个容器，键空间本来可以用前缀分开 |
| MinIO | `magic-multimedia-minio` + `magic-has-minio` | 原片和转码产物分属两个服务，has 还要跨实例读原片 |
| 生产编排 | `mini-local.yaml` 没有 multimedia、has、MinIO | 网关的 multimedia 路由在 prod 下无法解析 |

MySQL 仍是一个实例上的两个库（auth 与视频），本轮不合并。RabbitMQ 仍只有 multimedia → has 一条链路。

---

## 决策

### 1. 一个 Redis

容器名和主机名都是 `magic-redis`，容器端口 6379，逻辑库 `database: 0`。

| 用途 | 隔离方式 |
|---|---|
| Sa-Token 会话 | key 前缀 `sa-token:`（token 名） |
| core 缓存 | `spring.cache.redis.key-prefix: core:` |

dev 把 6379 映射到宿主机；local / prod 不映射。删除 `magic-auth-redis` 和宿主机端口 6389。

### 2. 一个业务 MinIO

容器名 `magic-minio`。容器内 API `9000`、控制台 `9001`，宿主机映射 **9010 / 9011**（三个编排文件一致）。

| bucket | 写入方 | 内容 |
|---|---|---|
| `raw` | multimedia | 图片、原片 |
| `transcoded` | has | HLS / DASH |

has 用转码消息里的预签名 URL 读取 `raw` 中的原片，不再连接第二个 MinIO。

`docker-compose-dev.yaml` 里的 `magic-loki-minio`（宿主机 9030/9031）只给 Loki，不存放 `raw` / `transcoded`。它不是业务双实例。

### 3. 生产编排补齐媒体链路

`mini-local.yaml` 包含 `magic-multimedia`、`magic-has` 和同一个 `magic-minio`。CI（`.github/workflows/prod.yml`）构建并推送 gateway、auth、core、multimedia、has 五个镜像，标签 `xiaolinstar/magic-<module>:v0.1`。

---

## 影响

- 换环境时只改一个 Redis 主机和一个 MinIO endpoint。
- 会话和缓存打在同一个进程上：Redis 宕机会同时踢掉登录态并丢掉 core 缓存。单机场景接受这个代价。
- 原片 bucket 的公开读策略没有在本轮收紧（技术债 P2-1 仍开放）。

---

## 备选方案与否决理由

| 方案 | 否决理由 |
|---|---|
| 继续两个 Redis，只统一文档 | 多一个必须健康的容器，键前缀已经够隔离 |
| 用 Redis database 1/2 代替前缀 | Sa-Token 与 Spring Cache 都已有前缀，再拆 DB 没有额外收益 |
| 保留两个 MinIO，用复制同步原片 | 单机上多一倍磁盘和密钥，转码链路更难排障 |
| 把 Loki 的 MinIO 也并进 `magic-minio` | 日志和媒资生命周期不同；本轮只收敛业务桶 |

---

## 验证

`docker-compose-local.yaml`、`docker-compose-dev.yaml`、`mini-local.yaml` 中业务服务只声明 `magic-redis` 与 `magic-minio`。`rg 'magic-auth-redis|6389'` 在这些编排和 `bootstrap-*.yaml` 中无匹配。

compose 烟测未在本环境执行（见 [ADR-0009](0009-remove-dubbo-http-openapi.md) 验证一节）。

---

## 关联文档

- [`../02-architecture.md`](../02-architecture.md)
- [`0005-minio-object-key.md`](0005-minio-object-key.md)
