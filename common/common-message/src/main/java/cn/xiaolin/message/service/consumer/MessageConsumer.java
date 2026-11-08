package cn.xiaolin.message.service.consumer;

import cn.xiaolin.message.entity.TranscodeMessage;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;

import java.io.IOException;

/**
 * @author xingxiaolin xing.xiaolin@foxmail.com
 * @Description
 * @create 2024/11/13
 */
public interface MessageConsumer {

    /**
     * 处理转码任务。
     *
     * <p>使用手动 ack：处理成功调用 {@code channel.basicAck(deliveryTag, false)}，
     * 处理失败调用 {@code channel.basicNack(deliveryTag, false, false)} 让消息进入死信队列，
     * 避免默认 auto-ack 下失败消息被无限重投。
     *
     * @param message    转码任务
     * @param rawMessage 原始消息，delivery tag 从中获取
     * @param channel    RabbitMQ 通道，用于手动 ack / nack
     */
    void pullTranscodeMessage(TranscodeMessage message, Message rawMessage, Channel channel) throws IOException;
}
