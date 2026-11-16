# 云原生架构烟测清单

手工检查主链路是否仍走 HTTP，以及编排里是否只有一个业务 Redis、一个业务 MinIO。

日期：2026-11-08

## 步骤

1. `docker compose -f docker-compose-local.yaml up -d --build`
2. 经 gateway `:9000` 注册并登录
3. 经 `/multimedia/**` 上传视频
4. 看 has 消费日志（ffmpeg，以及 HTTP 调用 core 保存播放源）
5. 在 MySQL 的 `video_source` 中核对对应行
6. `rg -i dubbo`（代码，排除 `docs/`）应无匹配

前置：JDK 17、Maven、Docker。镜像 Dockerfile 只 `COPY target/<module>-0.0.1-SNAPSHOT.jar`，需要先在仓库根目录执行 `mvn clean package -DskipTests`（或 `./mvnw`）。环境变量见根目录 `.local.env`（`MYSQL_INIT`、`MINIO_DATA`）。

上传接口需要 `common` 角色；新注册用户默认无角色。烟测可用种子用户 `li` / `123456li`。DASH 转码要求源片含音轨（`-map 0:a:0`）。

## 本次执行记录（2026-11-08）

| 步骤 | 结果 |
|---|---|
| 1–5 全栈 compose | **未执行**。宿主机没有 `java` 命令，`auth/target` 等目录也没有可拷贝的 jar。`docker compose build` 会在 `COPY target/*.jar` 失败。Docker 守护进程本身可用（Compose v5.5.1）。 |
| 6 代码检索 | 通过。`*.java`、`*.xml`、`*.yaml`、`pom.xml` 无 `dubbo`。README 仅说明该协议已删除。 |
| 编排静态检查 | `docker-compose-local.yaml` 与 `mini-local.yaml` 只有 `magic-redis`、`magic-minio`。`magic-auth-redis` 与端口 `6389` 未出现。dev compose 另有仅供 Loki 的 `magic-loki-minio`。 |

## 本次执行记录（2026-11-16）

| 步骤 | 结果 |
|---|---|
| 前置 | `./mvnw clean package -DskipTests` 成功。MinIO 镜像经 `docker.1panel.live/minio/minio:latest` 拉取并 tag 为 compose 所需标签（实际版本 `RELEASE.2025-09-07`）。`.local.env` 改为本机 WSL 路径。 |
| 1 compose | **通过（业务栈）**。跳过可观测性镜像。修复 Dockerfile：各服务预建 `/app/logs`；`has` 预建 `/home/magic/hls-video`、`dash-video`。gateway health `UP`；auth/core/multimedia/has healthy。 |
| 2 注册/登录 | **通过**。注册成功；上传需 `common` 角色，改用种子用户 `li`/`123456li` 登录（`Set-Cookie: sa-token=...`）。 |
| 3 上传 | **通过**。`POST /multimedia/video?videoType=MP4&videoId=10011` → `raw/videos/<key>.mp4`。 |
| 4 has 转码 | **通过**。HLS + DASH 成功；`POST` core internal 写播放源。无音轨样片会导致 DASH 失败入 DLQ（样片需含 AAC）。 |
| 5 `video_source` | **通过**。`video_id=10011` 新增 mp4 / hls / dash 三行，src 为 `http://magic-minio:9000/...` 稳定公开 URL。 |
| 6 代码检索 | **通过**。业务代码无 `dubbo`。 |
| 编排静态检查 | **通过**。单 `magic-redis`、单业务 `magic-minio`。 |

结论：云原生主链路（Gateway HTTP → 上传 → MQ → has ffmpeg → core `video_source`）在本机业务 compose 下已验收通过。
