# magic-video-backend 文档

> 这份文档是**设计文档**，不是使用文档。
> 使用说明（如何跑起来、API 列表、部署清单）见仓库根目录的 [README.md](../README.md) 与各模块 README。

## 文档目标

1. 把当前架构"应该长什么样"写下来，让代码与设计一一对应
2. 沉淀已落地的关键设计决策（ADR），新人不必靠口口相传理解"为什么"
3. 列出当前代码与设计之间的差距（技术债），作为后续 PR 的选题池
4. 描述演进路线，避免每次都说"将来要不要做 X"

## 目录约定

| 目录 / 文件 | 内容 | 更新时机 |
|---|---|---|
| `01-overview.md` | 项目愿景、用户场景、非功能需求 | 愿景变化时 |
| `02-architecture.md` | 服务清单、调用矩阵、部署形态 | 架构演进时 |
| `03-domain-model.md` | 领域模型、ER 图 | 新增业务实体时 |
| `04-use-cases.md` | 核心用例（用户故事级） | 业务流程变化时 |
| `05-api-contracts.md` | 对外 REST 说明（内部契约以 `contracts/` 为准） | API 变更时 |
| `06-data-flow.md` | 关键数据流（含时序图） | 链路变化时 |
| `07-config-and-deploy.md` | 配置管理与部署方式 | 部署形态变化时 |
| `08-roadmap.md` | 演进路线 | 路线变化时 |
| `09-tech-debt.md` | 当前技术债务清单 | 每完成一项或发现一项时 |
| `10-architecture-review.md` | 架构审查报告与设计优化建议 | 架构大改或定期复审时 |
| `adr/` | 架构决策记录（ADR） | 关键决策落地时 |

> ⚠️ 上表中 `01`、`03` ~ `08` 目前**尚未编写**，是规划中的文档；
> 现有文档只有 `02-architecture.md`、`09-tech-debt.md`、`10-architecture-review.md` 与 `adr/`。

## 编写约定

- **不写"将来可能"**：写"当前的设计"和"已知的债"
- **不重复代码细节**：架构图用 mermaid 注册一致风格
- **每个 ADR 必须有"备选方案"和"为什么否决"**：避免被后人质疑"为啥不那样"
- **技术债清单按 P0/P1/P2 分级**：每项带文件 + 行号 + 影响描述

## 当前状态

| 文档 | 状态 |
|---|---|
| 02-architecture.md | ✅ 2026-11-08 已改为 HTTP internal、单 Redis、单业务 MinIO |
| 09-tech-debt.md | ✅ 2026-11-08：本分支已关闭项已勾选；P2-2 缓存与 Flyway 仍开放 |
| 10-architecture-review.md | ✅ 2026-10-01 全面审查：含 3 处运行时实证验证 |
| `adr/0001-snowflake-id.md` | ✅ 已落地 |
| `adr/0002-pagination.md` | ✅ 已落地 |
| `adr/0003-cache-strategy.md` | ✅ 已落地 |
| `adr/0004-sa-token-public-route.md` | ✅ 已落地 |
| `adr/0005-minio-object-key.md` | ✅ 已落地 |
| `adr/0006-async-transcoding.md` | ✅ 已落地（方案调整为 MQ 队列有界并发，任务表未实现） |
| `adr/0007-rabbitmq-consumer-ack.md` | ✅ 已落地 |
| `adr/0008-remove-nacos.md` | ✅ 已落地（彻底移除 Nacos，改直连 + 本地配置） |
| `adr/0009-remove-dubbo-http-openapi.md` | ✅ 已落地（移除 Dubbo，内部 HTTP + OpenAPI） |
| `adr/0010-platform-and-contracts.md` | ✅ 已落地（`platform-web` + `contracts`，删除 `common/`） |
| `adr/0011-single-minio-redis.md` | ✅ 已落地（业务 Redis / MinIO 各一个实例） |

---

## 最近一次变更

**2026-11-08** —— 云原生分支落地（在 Phase 0 与移除 Nacos 之后）：

- 服务间同步改为 HTTP + OpenAPI，Dubbo 删除（ADR-0009）；
- `common/` 删除，共享库为 `platform-web`，契约在 `contracts/`（ADR-0010）；
- 业务 Redis、业务 MinIO 各一个实例；`mini-local` 含媒体链路（ADR-0011）；
- K8s 与服务网格仍是文档中的非目标，不交付可运行清单；
- compose 主链路烟测因环境没有 JDK 未重跑，步骤见 `superpowers/plans/smoke-checklist-cloud-native.md`。

更早同一天还完成了 Phase 0 断点修复、移除 Nacos（ADR-0008），以及 ADR-0006 / ADR-0007 按实际 MQ 实现改写。
