package cn.xiaolin.multimedia.mq;

/**
 * 转码管线的 Rabbit 生产者。
 */
public interface MessageProducer {

    /**
     * 发送到默认交换机（{@link MessageQueueConsts#EXCHANGE_MEDIA_RESOURCE}），routing key 为空。
     */
    void publish(ResourceMessage resourceMessage);

    /**
     * 发送到指定交换机，routing key 为空。
     */
    void publish(String exchange, ResourceMessage resourceMessage);

    /**
     * 发送到指定交换机与 routing key。
     */
    void publish(String exchange, String routingKey, ResourceMessage resourceMessage);

    /**
     * 投递转码任务。
     *
     * <p>消息体必须是 {@link TranscodeMessage}。直接发送 {@link java.net.URL} 等 JDK 类型会被消费端拒绝。
     */
    void sendTranscodeMessage(TranscodeMessage message);
}
