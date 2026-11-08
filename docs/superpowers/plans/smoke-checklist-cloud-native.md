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

前置：JDK 17、Maven、Docker。镜像 Dockerfile 只 `COPY target/<module>-0.0.1-SNAPSHOT.jar`，需要先在仓库根目录执行 `mvn clean package -DskipTests`。环境变量见根目录 `.local.env`（`MYSQL_INIT`、`MINIO_DATA`）。

## 本次执行记录（2026-11-08）

| 步骤 | 结果 |
|---|---|
| 1–5 全栈 compose | **未执行**。宿主机没有 `java` 命令，`auth/target` 等目录也没有可拷贝的 jar。`docker compose build` 会在 `COPY target/*.jar` 失败。Docker 守护进程本身可用（Compose v5.5.1）。 |
| 6 代码检索 | 通过。`*.java`、`*.xml`、`*.yaml`、`pom.xml` 无 `dubbo`。README 仅说明该协议已删除。 |
| 编排静态检查 | `docker-compose-local.yaml` 与 `mini-local.yaml` 只有 `magic-redis`、`magic-minio`。`magic-auth-redis` 与端口 `6389` 未出现。dev compose 另有仅供 Loki 的 `magic-loki-minio`。 |

有 JDK 之后从步骤 1 重跑。不要把本次「未执行」写成主链路已通过。
