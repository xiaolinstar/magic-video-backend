# ADR-0007 · RabbitMQ 消费者：手动 ack + 死信队列

- 状态：✅ 已落地（2026-11-08）
- 日期：2026-10-01（决策定稿） / 2026-11-08（落地，方案有调整）
- 关联：技术债 P0-3 / P1-4

## 背景

`has/.../MessageConsumerImpl.pullUrlMessage(URL url)` 原先默认 auto-ack：

- ffmpeg 抛异常时 RabbitMQ 默认行为取决于配置（重投 / 丢弃 / 进死信）
- **没有配置死信队列**，失败消息无去处
- 客户端发布消息后没有反馈"已被受理"

2026-11-08 的落地过程中还暴露了一个更前置的问题：消息体是 `java.net.URL`，
消费端因 Jackson 可信包校验失败直接抛异常，**消息永远无法被消费**，
叠加 auto-ack + 无 DLQ → 变成无限重投的毒消息循环。
该问题由 [P0-1](../09-tech-debt.md) 解决（消息体改为 `TranscodeMessage` DTO）。

## 决策

### 1. 手动 ack

**注意**：Spring AMQP 3.1.12 中**不存在** `org.springframework.amqp.core.Acknowledgment` 类
（已全量检索 m2 仓库确认），因此不能使用 `Acknowledgment` 参数。
采用通用的 `Channel` + `Message`（取 delivery tag）模式：

```java
@RabbitListener(
        queues = {MessageQueueConsts.QUEUE_MEDIA_RESOURCE},
        ackMode = "MANUAL",
        concurrency = "${app.transcode.concurrency:2}"
)
public void pullTranscodeMessage(TranscodeMessage message, Message rawMessage, Channel channel)
        throws IOException {
    long deliveryTag = rawMessage.getMessageProperties().getDeliveryTag();
    try {
        // 转码 + 回写 core
        channel.basicAck(deliveryTag, false);
    } catch (Exception e) {
        // 不重投：交给死信队列，避免毒消息循环
        channel.basicNack(deliveryTag, false, false);
    }
}
```

**异常分类**：当前实现不区分业务异常与系统异常，统一 `nack(requeue=false)` 进死信队列。
理由：转码失败的常见原因（源文件损坏、ffmpeg 不可用）重试也不会成功；
若要区分，需要先有「任务表 + 状态机」（见 ADR-0006 的后续演进）。

### 2. 死信队列（DLQ）

交换机 `xiaolin.media.dlx` + 队列 `video.resource.dlq`，主队列通过
`x-dead-letter-exchange` 参数指向它：

```java
@Bean("resourceQueue")
public Queue resourceFanoutQueue() {
    return new Queue(
            MessageQueueConsts.QUEUE_MEDIA_RESOURCE,
            true, false, false,
            Map.of("x-dead-letter-exchange", MessageQueueConsts.EXCHANGE_MEDIA_RESOURCE_DLQ)
    );
}
```

> **历史队列兼容**：RabbitMQ 不允许参数不等价时重新声明同名队列，
> 已有环境中的 `video.resource`（没有 DLX 参数）会触发 `PRECONDITION_FAILED` 导致启动失败。
> `RabbitMQConfig` 自定义了 `RabbitAdmin`：声明失败且确认为 `PRECONDITION_FAILED` 时，
> 删除旧队列后重建。旧队列残留的是 `java.net.URL` 类型的毒消息，丢弃无损失。

### 3. 并发度

`concurrency` 由配置 `app.transcode.concurrency` 控制（默认 2）。

**不用独立线程池**：`@RabbitListener` 的并发度本身就是「有界并发」——
N 个消费者线程，每个同时只处理一个转码任务。相比"消费者线程 + 线程池"的两段式，
少一层调度、少一次跨线程 ack 的线程安全风险，且背压天然由 MQ 队列承担。

## 影响

- ✅ 转码失败有兜底（DLQ 可人工干预）
- ✅ 消息不会被无限重投（原队列不再残留）
- ✅ 并发度有界，不会因多个长任务耗尽线程
- ⚠️ 需要修改 `RabbitMQConfig` 加 DLQ 配置（已做）
- ⚠️ 需要自定义 `RabbitAdmin` 处理历史队列（已做）

## 备选

| 方案 | 否决理由 |
|---|---|
| 关闭 DLQ，靠重试到天荒地老 | 失败任务阻塞队列，没出口 |
| 用 RabbitMQ Delayed Message 插件做指数退避 | 依赖额外插件，单机环境尽量少装 |
| 消费者收消息 → 写任务表 → ack，ffmpeg 由独立线程池执行 | 需要新增任务表与状态机，是更大的改造；当前单机单实例场景下，**有界并发已足够**（见 ADR-0006） |
| **手动 ack + 死信队列 + 有界并发** | **采用** |

## 落地清单

- [x] `RabbitMQConfig` 加 DLQ Exchange / Queue / Binding，主队列声明 `x-dead-letter-exchange`
- [x] 自定义 `RabbitAdmin`，处理历史队列参数不等价导致的声明失败
- [x] `MessageConsumerImpl` 改为手动 ack（`Channel` + delivery tag）
- [x] 消息体改为 `TranscodeMessage`（受信包内 DTO），消除可信包校验失败
- [x] `app.transcode.concurrency` 配置化

## 验证

- `TranscodeMessageContractTest` —— 消息经真实 RabbitMQ 往返成功（此前永远收不到）
- `DeadLetterRoutingTest` —— 消费失败的消息进入死信队列，原队列消息数归零
