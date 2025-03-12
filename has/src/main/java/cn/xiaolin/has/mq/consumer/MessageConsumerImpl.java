package cn.xiaolin.has.mq.consumer;

import cn.xiaolin.has.service.MediaKitService;
import cn.xiaolin.message.constant.MessageQueueConsts;
import cn.xiaolin.message.entity.ResourceMessage;
import cn.xiaolin.message.service.consumer.MessageConsumer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.net.URL;

/**
 * @author xingxiaolin xing.xiaolin@foxmail.com
 * @Description RabbitQueue消息消费者
 * @create 2024/11/11
 */

@Slf4j
@Service
@RequiredArgsConstructor
public class MessageConsumerImpl implements MessageConsumer {

    private final MediaKitService mediaKitService;

    @Override
    @RabbitListener(queues = {MessageQueueConsts.QUEUE_MEDIA_RESOURCE})
    public void pullUrlMessage(URL url) {
        log.info("接收到URL：{}", url);
        mediaKitService.media2Dash(url.toString());
    }
}

