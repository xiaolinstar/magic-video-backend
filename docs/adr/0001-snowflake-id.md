# ADR-0001 · 主键生成策略：雪花算法

- 状态：✅ 已落地
- 日期：2026-10-01
- 落地提交：（本次 review 中）

## 背景

所有 entity 之前用 `@TableId` 但没指定 `type`，MyBatis-Plus 默认按 NONE 策略 → 主键由调用方传入，否则插 0。**上游忘记传 id 会写一行 id=0 的数据，多实例多数据库几乎必然主键冲突**。

DDL 注释明确写"雪花算法"，但代码层面无体现。

## 决策

所有 entity 的 `@TableId` 显式声明 `type = IdType.ASSIGN_ID`（MyBatis-Plus 内置雪花算法实现）。

涉及 21 个 entity（auth 6 + core 15），已批量替换。

## 影响

- ✅ 上游不需要手动生成 id，MyBatis-Plus 在 insert 前自动填充
- ✅ 19 位 Long 在 MySQL `BIGINT` 范围内（2^63 - 1 = 9.22e18）
- ✅ 集群节点隔离：每个服务实例通过 `dubbo.application.name` + workerId 区分，雪花冲突概率极低
- ⚠️ DDL 没变：现有种子脚本用 `(1001, ...)` 等 4 位 id 仍能插入（雪花 id 是 19 位 Long）
- ⚠️ 既有数据不冲突：雪花生成的是新数据，老数据保留原 id

## 备选方案

| 方案 | 优点 | 否决理由 |
|---|---|---|
| UUID v4 | 无中心化、无冲突 | UUID 是字符串，索引体积大 |
| UUID v7 | 时序性、字符串索引优化 | MySQL 不原生支持，需要应用层转 long |
| Redis INCR | 强一致、可观测 | 引入外部依赖 + 单点 |
| 数据库 AUTO_INCREMENT | 简单 | 多库全局不唯一、无法支持分库 |
| **雪花算法** | **19 位、时序、本地生成** | **采用** |

## 注意点

- 雪花算法依赖 workerId (10 bit) 和 datacenterId (5 bit)。MyBatis-Plus 默认通过 `IdType.ASSIGN_ID` 的内置 `DefaultIdentifierGenerator` 用 `InetAddress.getLocalHost().getAddress()` 哈希出 15 bit 节点信息
- 单机多网卡环境可能 workerId 冲突，极小概率但生产前应验证
- 分布式多节点部署时若 workerId 冲突可配置 `mybatis-plus.global-config.worker-id` 手动指定
