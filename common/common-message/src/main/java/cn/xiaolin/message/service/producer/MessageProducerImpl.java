package cn.xiaolin.message.service.producer;

import cn.xiaolin.message.constant.MessageQueueConsts;
import cn.xiaolin.message.entity.ResourceMessage;
import cn.xiaolin.message.entity.TranscodeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

/**
 * 发送异步消息，消息生产者
 *
 * @author xingxiaolin xing.xiaolin@foxmail.com
 * @Description
 * @create 2024/11/13
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class MessageProducerImpl implements MessageProducer {

    /** 默认 routing key：fanout 交换机忽略 routing key，此处仅为满足 API 签名 */
    private static final String DEFAULT_ROUTING_KEY = "";

    private final RabbitTemplate rabbitTemplate;

    @Override
    public void publish(ResourceMessage resourceMessage) {
        rabbitTemplate.convertAndSend(MessageQueueConsts.EXCHANGE_MEDIA_RESOURCE, DEFAULT_ROUTING_KEY, resourceMessage);
    }

    @Override
    public void publish(String exchange, ResourceMessage resourceMessage) {
        rabbitTemplate.convertAndSend(exchange, DEFAULT_ROUTING_KEY, resourceMessage);
    }

    @Override
    public void publish(String exchange, String routingKey, ResourceMessage resourceMessage) {
        rabbitTemplate.convertAndSend(exchange, routingKey, resourceMessage);
    }

    @Override
    public void sendTranscodeMessage(TranscodeMessage message) {
        if (message == null || message.getSourceUrl() == null || message.getSourceUrl().isBlank()) {
            throw new IllegalArgumentException("转码消息缺少 sourceUrl");
        }
        log.info("投递转码任务：videoId={}, resourceKey={}", message.getVideoId(), message.getResourceKey());
        rabbitTemplate.convertAndSend(MessageQueueConsts.EXCHANGE_MEDIA_RESOURCE, DEFAULT_ROUTING_KEY, message);
    }

}
