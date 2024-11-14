package cn.xiaolin.has.mq.consumer;

import cn.xiaolin.message.constant.MessageQueueConsts;
import cn.xiaolin.message.entity.ResourceMessage;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

/**
 * @author xingxiaolin xing.xiaolin@foxmail.com
 * @Description RabbitQueue消息消费者
 * @create 2024/11/11
 */

@Service
public class ResourceConsumerImpl {

    @RabbitListener(queues = {MessageQueueConsts.QUEUE_MEDIA_RESOURCE})
    public void consume(ResourceMessage resourceMessage) {
        System.out.println("resource消费 " + resourceMessage);
    }

}

