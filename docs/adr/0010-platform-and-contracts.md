# ADR-0010 · `common` 收敛为 `platform-web` + `contracts`

- 状态：✅ 已落地
- 日期：2026-11-08
- 关联：[ADR-0009](0009-remove-dubbo-http-openapi.md)；设计规格第 4 节

---

## 背景

`common` 同时装着四件不相干的事：

| 模块 | 实际内容 | 问题 |
|---|---|---|
| `common-api` | Dubbo 接口 + 传输 DTO | 二进制 jar 充当 RPC 契约 |
| `common-utils` | `Result`、异常、分页、Jackson、CORS | 无业务语义，却和 RPC 捆在同一棵树 |
| `common-db` | MyBatis-Plus 配置、审计填充 | 只有 auth / core 需要 |
| `common-message` | 交换机、队列、生产/消费 | 生产在 multimedia，消费在 has，库模块两边都依赖 |

根 reactor 若继续聚合这棵树，服务之间会通过共享实体和 Mapper 形成事实上的分布式单体。

---

## 决策

删除 `common/`。共享代码只留无业务语义的 web 基础库；服务间契约只留 YAML。

```
magic-video-backend/
├── contracts/                 # 不是 Maven 模块
│   ├── openapi/               # auth-internal.v1.yaml、core-internal.v1.yaml
│   └── asyncapi/              # transcode.v1.yaml
├── platform/
│   └── platform-web/          # Result、异常、分页、Jackson
├── auth/  core/               # MyBatis-Plus 配置内联
├── multimedia/                # MQ 生产
└── has/                       # MQ 消费
```

### 去向

| 原模块 | 处理 |
|---|---|
| `common-api` | 删除。HTTP/MQ 契约改到 `contracts/` |
| `common-utils` | 迁为 `platform-web` |
| `common-db` | 配置复制进 auth、core 后删除 |
| `common-message` | 拓扑写入 AsyncAPI；生产实现留在 multimedia，消费实现留在 has |

包名暂留 `cn.xiaolin.utils`，减小 diff。改成 `cn.xiaolin.platform.web` 不在本轮。

### 依赖规则

1. 业务服务可以依赖 `platform-web`，不能依赖另一个业务服务，也不能依赖已删除的 `common-*`。
2. `platform-web` 不依赖业务模块、Dubbo、具体 MQ 业务类。
3. 服务之间只通过 HTTP（OpenAPI）或 MQ（AsyncAPI）通信，不共享实体或 Mapper。
4. 父 POM 统一版本是仓库组织方式，不是把五个进程合成一个单体。
5. 本轮不引入 openapi-generator / asyncapi-codegen。消费方手写与 YAML 字段一致的薄 DTO。

---

## 影响

- 根 `<modules>` 为 `platform`、`auth`、`core`、`gateway`、`multimedia`、`has`。`contracts/` 不参与 Maven 反应堆。
- 改内部 API 时先改 YAML，再改实现和测试。
- CORS 等网关相关配置若仍留在 `platform-web`，后续应再挪到 gateway；本轮不扩大这次拆分。

---

## 备选方案与否决理由

| 方案 | 否决理由 |
|---|---|
| 保留 `common`，只删 `common-api` | db 与 message 仍把无关服务编进同一个库 |
| 每个服务复制一份 `Result` | 错误码和分页会立刻分叉 |
| 用生成器从 OpenAPI 产出 DTO | 本轮服务数量少，生成插件和包名迁移的成本更高 |

---

## 验证

- 磁盘上不再有 `common/` 目录。
- 五个服务的 POM 依赖 `platform-web`，没有 `common-api`。
- `contracts/openapi/*.yaml` 与 auth/core 的 internal Controller、gateway/has 客户端路径一致。

---

## 关联文档

- [`contracts/README.md`](../../contracts/README.md)
- [`0009-remove-dubbo-http-openapi.md`](0009-remove-dubbo-http-openapi.md)
