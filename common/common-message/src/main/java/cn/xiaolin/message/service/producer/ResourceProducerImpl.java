package cn.xiaolin.message.service.producer;

import cn.xiaolin.message.constant.MessageQueueConsts;
import cn.xiaolin.message.entity.ResourceMessage;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.NotImplementedException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

/**
 * @author xingxiaolin xing.xiaolin@foxmail.com
 * @Description
 * @create 2024/11/13
 */

@Component
@RequiredArgsConstructor
public class ResourceProducerImpl implements ResourceProducer{
    private final RabbitTemplate rabbitTemplate;

    @Override
    public void publish(ResourceMessage resourceMessage) {
        rabbitTemplate.convertAndSend(MessageQueueConsts.EXCHANGE_MEDIA_RESOURCE, "", resourceMessage);
    }

    @Override
    public void publish(String exchange, ResourceMessage resourceMessage) {
        throw new NotImplementedException();
    }

    @Override
    public void publish(String exchange, String routingKey, ResourceMessage resourceMessage) {
        throw new NotImplementedException();
    }
}
