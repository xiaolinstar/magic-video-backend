# Contracts

本目录是服务间 HTTP（OpenAPI）与 MQ（AsyncAPI）的**唯一契约源**。实现方须在 Task 3–6 中与此处路径、字段、队列名保持一致；本轮**不**引入 openapi-generator / asyncapi-codegen，消费方在各自模块内手写薄 DTO 即可。

## 布局

| 文件 | 提供方 | 消费方 |
|------|--------|--------|
| `openapi/auth-internal.v1.yaml` | `auth` | `gateway`（权限/角色查询） |
| `openapi/core-internal.v1.yaml` | `core` | `has`（转码结果落库） |
| `asyncapi/transcode.v1.yaml` | `multimedia`（生产） | `has`（消费） |

版本后缀（`.v1`）表示破坏性变更时递增文件名或 `info.version`，并同步更新实现与测试。

## OpenAPI

- 基路径：各服务在集群内暴露 `/api/v1/internal/**`（compose 下不映射到宿主机，依赖网络隔离）。
- 信封：`Result<T>` 与 `platform/platform-web` 中 `cn.xiaolin.utils.resp.Result`、`ResponseCode` 一致；成功时 `code: 200`、`msg: 操作成功`。
- 校验：可用 [Swagger Editor](https://editor.swagger.io/) 或 `npx @redocly/cli lint openapi/*.yaml` 做静态检查（可选）。

## AsyncAPI

- Broker：RabbitMQ；交换机/队列常量见 `multimedia` 与 `has` 的 `MessageQueueConsts`（与 YAML 中名称一致）。
- 主路径载荷：`TranscodeMessage`（`sourceUrl`、`videoId`、`resourceKey`）；`ResourceMessage`（`id`、`md5`、`mp4`、`m3u8`、`mpd`）在同一拓扑上作为补充形态文档化。两端各持一份 JSON 字段相同的 DTO。
- 往返行为：`multimedia` 中 `TranscodeMessageContractTest` 对真实 broker + Jackson 可信包白名单做契约测试，修改 YAML 时请同步审视该测试与实体字段。

## 修改流程

1. 先改本目录 YAML，再改实现与测试。
2. PR 说明中注明契约版本与受影响服务。
3. 禁止从 `common-api` 等已废弃 jar 反向复制类型；契约只向前流动。
