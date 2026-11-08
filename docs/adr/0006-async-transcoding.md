# ADR-0006 · HAS 转码异步化

- 状态：✅ 已落地（2026-11-08，方案在落地时调整）
- 日期：2026-10-01（决策初稿） / 2026-11-08（落地，改为队列有界并发）
- 关联：技术债 P0-6 / P1-5

---

## 背景

`has/.../MediaKitServiceImpl.media2Hls` / `media2Dash` 用 `ProcessBuilder.start() + waitFor()` **同步等待** ffmpeg 完成。

- 一部 4GB 视频转 HLS 需要 30+ 分钟
- Tomcat 默认线程池 200 → **2 个并发转码耗尽工作线程**
- 同步等待期间其他 HTTP 请求全部 503
- 没有 timeout，长视频可能挂死
- 没有磁盘清理，HLS 临时目录会撑满

更关键的是：转码任务是**由 RabbitMQ 消息触发的**（`multimedia` 上传完成后投递）。
也就是说，一个纯消息消费者服务，却在**占用唯一一条 MQ 消费线程**做几十分钟的同步计算 ——
`@RabbitListener` 默认 `concurrency=1`，**整个 has 服务卡死在一个视频上**。

---

## 决策

### 1. 异步化的承载物：RabbitMQ 队列本身

**转码不再由"HTTP 线程"或"模块内线程池"承载，而是由 MQ 消费者线程承载。**

```
multimedia ──publish──▶ RabbitMQ 队列 ──consume──▶ has 消费者线程（并发 2）──▶ ffmpeg
                            ▲                                              │
                            └────────────── 失败 nack(requeue=false) ─────┴──▶ DLQ
```

- `@RabbitListener(concurrency = "${app.transcode.concurrency:2}")`：**2 个消费者线程**，
  每个同时只处理一个转码任务；
- **有界并发**：并发度就是上限，不会因任务堆积无限增线程；
- **背压天然由队列承担**：任务多时堆在队列里，不占应用线程；
- **HTTP 线程池与 ffmpeg 完全解耦**：has 没有对外 HTTP 接口，Tomcat 线程不参与转码。

### 2. ffmpeg 执行超时

`ProcessBuilder.waitFor()` 无超时参数 → 源站网络挂起时消费线程被永久占用。
改为带超时的等待，超时后强制结束进程（`MediaKitServiceImpl.runFfmpeg`）：

```java
boolean finished = process.waitFor(transcodeTimeoutMinutes, TimeUnit.MINUTES);
if (!finished) {
    process.destroyForcibly();
    log.error("{} 转码超时（超过 {} 分钟），已强制结束进程", format, transcodeTimeoutMinutes);
    return -1;                      // 退出码 -1 → 上层按「转码失败」处理
}
```

超时时长由 `app.transcode-timeout-minutes` 配置，**默认 120 分钟**。

> 同一方法还做了两件事：ffmpeg 的 stdout/stderr **只保留尾部**（大文件转码会刷出海量日志，
> 且管道写满会让 ffmpeg 阻塞）；捕获 `InterruptedException` 时**恢复中断标志**并强制结束进程。

### 3. 幂等：产物已存在则跳过转码

重复投递（MQ 重投、人工重复触发）不应重复转码、重复覆盖 MinIO 产物。
`media2Hls` / `media2Dash` 在转码前先检查 MinIO 中目标对象是否存在：

```java
if (objectExists(hlsBucketName, objectKey)) {
    log.info("HLS 产物已存在，跳过转码：{}", objectKey);
    return presignedUrl(hlsBucketName, objectKey);
}
```

> ⚠️ **幂等的代价**：任务重试语义被削弱 —— 首次失败后产物不存在，重投会重新转码（正确）；
> 但如果转码"成功但回写 core 失败"，重投会命中幂等分支直接复用产物（也正确）。
> 代价是**无法区分"这次真的重跑了"和"这次跳过了"**，日志里靠 `跳过转码` 关键字区分。

### 4. 临时目录转码后即删

`prepareWorkDir` 建工作目录 → ffmpeg 输出 → 上传 MinIO → `finally { cleanupWorkDir(...) }`。
上传成功或失败都会清理，避免 `~/hls-video/`、`~/dash-video/` 撑满磁盘。

### 5. 失败进入死信队列

转码或回写任一步失败 → 抛异常 → 消费者 `basicNack(deliveryTag, false, false)`（不重投）
→ 消息进入 `video.resource.dlq`。详见 [ADR-0007](0007-rabbitmq-consumer-ack.md)。

---

## 与初稿方案的差异（为什么改了）

初稿（2026-10-01）的方案是「**HAS 模块内部异步化**」：

> - 投递到模块内的 `ThreadPoolTaskExecutor`
> - 同步方法立即返回"已受理"，客户端轮询状态
> - 状态机 `[WAITING] → [RUNNING] → [SUCCEEDED] / [FAILED] → [RETRY_QUEUED]`
> - 存储在 `transcode_task` 表

**落地时这套方案被整体否决**，原因是：

| 初稿方案 | 落地时的实际情况 | 否决理由 |
|---|---|---|
| 模块内 `ThreadPoolTaskExecutor` | 改用 `@RabbitListener` 的 `concurrency` | 消费者线程**本来就在等消息**，让它直接干 ffmpeg 即可；再加一层线程池 = 多一次跨线程 ack 的线程安全风险 + 多一层调度 |
| `transcode_task` 表 + 状态机 | **未实现** | 需要新增表、entity、Mapper、状态流转 API 与清理任务；对当前单机单实例场景收益极低 |
| 客户端轮询任务状态 | **未实现** | `has` 没有任何对外 HTTP 接口，做状态查询要先给 `has` 开 HTTP 端点（破坏模块边界） |
| 失败重试最多 3 次 | 改为失败直接进 DLQ | 重试的前提是有任务表能判断"重了几次"；无状态机时重试次数无处记录 |

**一句话**：转码任务是「上传触发的一次性计算」，不是「有生命周期的业务实体」。
在单机单实例下，用 MQ 队列的有界并发就足够；建任务表是**为不需要的状态付费**。

---

## 影响

### 正面

- ✅ **has 服务不再被单个视频卡死**（并发 2，其余任务排队）
- ✅ **消费线程不会被挂死的网络请求永久占用**（ffmpeg 超时强制 kill）
- ✅ **失败有兜底**（DLQ 可人工干预，不会无限重投）
- ✅ **重复投递不重复计算**（MinIO 幂等复用）
- ✅ **磁盘不会因转码产物被撑满**（临时目录即用即删）

### 负面 / 已知缺口

- ⚠️ **转码进度不可查询**：没有任务表，没有状态机，客户端只能看到"视频还没出播放源"。
  当前只能靠 `has` 日志（`收到转码任务` / `转码任务完成`）判断。
- ⚠️ **失败不可自动重试**：消息直接进 DLQ，需要人工从 DLQ 重新投递。
  想要自动重试，必须先有任务表（回到上一节的取舍）。
- ⚠️ **并发度写死在配置里**：调大 `app.transcode.concurrency` 会同步放大 CPU/内存占用
  （每个并发一个 ffmpeg 进程），单机上建议 ≤ CPU 核数。

---

## 备选方案与否决理由

| 方案 | 否决理由 |
|---|---|
| `@Async` 注解 | 执行状态不可观测，异常被吞；且需要自建线程池，等于回到"消费者线程 + 线程池"两段式 |
| Spring Batch Job | 杀鸡用牛刀；单场景单任务，用不上批处理框架 |
| 独立 `transcoder-worker` 服务 | 单机场景下多一个常驻容器，多一套网络与配置；当前转码量根本撑不起独立部署 |
| 任务表 + 状态机 + HTTP 状态查询 | 完整但过重；需要新增表 / entity / Mapper / API / 清理任务，见「与初稿方案的差异」 |
| **MQ 队列 + `@RabbitListener` 有界并发 + ffmpeg 超时** | **采用**：少一层调度、背压由 MQ 承担、并发有界、失败有 DLQ 兜底 |

---

## 落地清单

- [x] `@RabbitListener` 加 `concurrency = "${app.transcode.concurrency:2}"`（有界并发）
- [x] `ackMode = "MANUAL"` + `Channel` 手动 ack（详见 [ADR-0007](0007-rabbitmq-consumer-ack.md)）
- [x] `runFfmpeg` 改用 `waitFor(timeout, unit)` + `destroyForcibly()`
- [x] 新增 `app.transcode-timeout-minutes` 配置（默认 120 分钟）
- [x] 幂等：转码前检查 MinIO 产物是否存在，存在则复用
- [x] 临时目录 `finally` 清理
- [x] 失败 `basicNack(requeue=false)` → DLQ
- [ ] 任务表 + 状态机 + 状态查询接口 —— **未实现**，见「负面 / 已知缺口」

---

## 验证

| 验证项 | 方式 | 结果 |
|---|---|---|
| 消费端能收到消息 | `TranscodeMessageContractTest`（真实 RabbitMQ 往返） | ✅ 通过 |
| 失败消息进死信队列 | `DeadLetterRoutingTest`（真实 broker，原队列消息数归零） | ✅ 通过 |
| 队列参数 | `curl -u ... /api/queues/%2Fdev-media` | ✅ `x-dead-letter-exchange: xiaolin.media.dlx` |

> ⚠️ ffmpeg 转码本身的**端到端运行时验证**未完成 —— 沙盒环境拉不到 `minio/minio` 镜像，
> `has` 无法连接对象存储。上表的集成测试覆盖的是消息链路与队列路由，
> ffmpeg 侧的改动（超时、幂等、清理）通过代码走查确认。

---

## 关联文档

- [ADR-0007](0007-rabbitmq-consumer-ack.md) — 手动 ack、DLQ、并发度的具体实现
- [`09-tech-debt.md`](../09-tech-debt.md) — P0-6 / P1-5
- [`10-architecture-review.md`](../10-architecture-review.md) — P0-6 的原始问题描述
