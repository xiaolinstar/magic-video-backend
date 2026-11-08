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
| `05-api-contracts.md` | REST + Dubbo 接口契约 | API 变更时 |
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
| 02-architecture.md | ✅ 初稿（基于代码反推） |
| 09-tech-debt.md | ✅ 2026-11-08 复核：Phase 0 / Phase 1 已闭环，P0/P1-4/P1-10/P1-11 已关闭 |
| 10-architecture-review.md | ✅ 2026-10-01 全面审查：含 3 处运行时实证验证 |
| `adr/0001-snowflake-id.md` | ✅ 已落地 |
| `adr/0002-pagination.md` | ✅ 已落地 |
| `adr/0003-cache-strategy.md` | ✅ 已落地 |
| `adr/0004-sa-token-public-route.md` | ✅ 已落地 |
| `adr/0005-minio-object-key.md` | ✅ 已落地 |
| `adr/0006-async-transcoding.md` | ✅ 已落地（方案调整为 MQ 队列有界并发，任务表未实现） |
| `adr/0007-rabbitmq-consumer-ack.md` | ✅ 已落地 |
| `adr/0008-remove-nacos.md` | ✅ 已落地（彻底移除 Nacos，改直连 + 本地配置） |

---

## 最近一次变更

**2026-11-08** —— Phase 0 断点修复 + 路线 B（移除 Nacos）落地：

- Phase 0 的 9 个 P0 全部修复并通过实证（见 [`09-tech-debt.md` 修复进度跟踪](09-tech-debt.md#修复进度跟踪)）；
- **Nacos 被彻底移除**：网关路由与 Dubbo 改为直连地址，配置回归各服务 `bootstrap-{profile}.yaml`；
- 新增 3 个真实中间件集成测试：`TranscodeMessageContractTest`、`DeadLetterRoutingTest`、`DubboResourceServiceTest`；
- ADR-0006 改写为实际实现（MQ 有界并发 + ffmpeg 超时，**无任务表/状态机**）；
- ADR-0007 改写为实际实现（手动 ack + DLQ + 历史队列兼容）；
- 新增 ADR-0008 记录移除 Nacos 的决策与备选方案。
