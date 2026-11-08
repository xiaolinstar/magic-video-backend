package cn.xiaolin.message.service.producer;

import cn.xiaolin.message.entity.ResourceMessage;
import cn.xiaolin.message.entity.TranscodeMessage;

/**
 * @author xingxiaolin xing.xiaolin@foxmail.com
 * @Description
 * @create 2024/11/11
 */
public interface MessageProducer {

    /**
     * 发送到默认交换机（{@link cn.xiaolin.message.constant.MessageQueueConsts#EXCHANGE_MEDIA_RESOURCE}），
     * routing key 为空。
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
     * <p>消息体必须是 {@link TranscodeMessage} 这类位于可信包白名单内的 DTO，
     * 直接发送 {@link java.net.URL} 等 JDK 类型会被消费端的 Jackson 可信包校验拒绝。
     */
    void sendTranscodeMessage(TranscodeMessage message);
}
