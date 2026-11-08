package cn.xiaolin.has.mq.consumer;

import cn.xiaolin.api.domain.dto.ResourceDto;
import cn.xiaolin.has.dubbo.comsumer.DubboResourceClient;
import cn.xiaolin.has.service.MediaKitService;
import cn.xiaolin.message.constant.MessageQueueConsts;
import cn.xiaolin.message.entity.TranscodeMessage;
import cn.xiaolin.message.service.consumer.MessageConsumer;
import com.rabbitmq.client.Channel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * RabbitQueue消息消费者
 *
 * <p>手动 ack：转码与回写全部成功才 ack；任一步失败都 nack 到死信队列，
 * 避免失败消息在原队列中无限重投。
 *
 * @author xingxiaolin xing.xiaolin@foxmail.com
 * @Description
 * @create 2024/11/11
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MessageConsumerImpl implements MessageConsumer {

    private final MediaKitService mediaKitService;
    private final DubboResourceClient dubboResourceClient;

    @Override
    @RabbitListener(
            queues = {MessageQueueConsts.QUEUE_MEDIA_RESOURCE},
            ackMode = "MANUAL",
            concurrency = "${app.transcode.concurrency:2}"
    )
    public void pullTranscodeMessage(TranscodeMessage message, Message rawMessage, Channel channel) throws IOException {
        long deliveryTag = rawMessage.getMessageProperties().getDeliveryTag();

        if (message == null || message.getSourceUrl() == null || message.getSourceUrl().isBlank()) {
            log.error("转码消息缺少 sourceUrl，直接进入死信队列：{}", message);
            channel.basicNack(deliveryTag, false, false);
            return;
        }
        if (message.getVideoId() == null) {
            log.error("转码消息缺少 videoId，无法关联播放源，直接进入死信队列：{}", message);
            channel.basicNack(deliveryTag, false, false);
            return;
        }

        try {
            log.info("收到转码任务：videoId={}, resourceKey={}, sourceUrl={}",
                    message.getVideoId(), message.getResourceKey(), message.getSourceUrl());

            // 幂等由 MediaKitService 负责：产物已存在则跳过转码直接复用
            String m3u8Url = mediaKitService.media2Hls(message.getSourceUrl(), message.getResourceKey());
            String mpdUrl = mediaKitService.media2Dash(message.getSourceUrl(), message.getResourceKey());

            // 回写 core：把播放源落到 video_source 表
            ResourceDto resourceDto = ResourceDto.builder()
                    .id(message.getVideoId())
                    .md5(message.getResourceKey())
                    .mp4(message.getSourceUrl())
                    .m3u8(m3u8Url)
                    .mpd(mpdUrl)
                    .build();
            dubboResourceClient.saveResource(resourceDto);

            channel.basicAck(deliveryTag, false);
            log.info("转码任务完成：videoId={}, m3u8={}, mpd={}",
                    message.getVideoId(), m3u8Url, mpdUrl);
        } catch (Exception e) {
            log.error("转码任务处理失败，消息进入死信队列：{}", message, e);
            channel.basicNack(deliveryTag, false, false);
        }
    }
}
