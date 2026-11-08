package cn.xiaolin.message;

import cn.xiaolin.message.constant.MessageQueueConsts;
import cn.xiaolin.message.entity.TranscodeMessage;
import cn.xiaolin.message.service.producer.MessageProducer;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 转码消息契约的**真实 broker** 往返测试。
 *
 * <p>这条链路此前是断的：生产端发送 {@code java.net.URL} 对象，
 * 消费端因 Jackson 可信包白名单校验失败而抛
 * {@code IllegalArgumentException: The class 'java.net.URL' is not in the trusted packages}，
 * 消息永远无法被消费，最终在队列里变成无限重投的毒消息。
 *
 * <p>本测试用真实 RabbitMQ + 真实 {@code Jackson2JsonMessageConverter}
 * （由 {@code RabbitMQConfig} 提供，带可信包白名单）验证消息能被正确投递并还原。
 */
@SpringBootTest
class TranscodeMessageContractTest {

    /** 捕获到的消息 */
    private static final BlockingQueue<TranscodeMessage> RECEIVED = new ArrayBlockingQueue<>(10);

    @Autowired
    private MessageProducer messageProducer;

    /**
     * 监听器单独放成 Bean：{@code @SpringBootTest} 不会把测试类本身注册为 Bean，
     * 而 {@code @RabbitListener} 需要容器能发现它。
     *
     * <p>队列与交换机由应用自身的 {@code RabbitMQConfig} 声明（含死信交换机参数），
     * 这里不再重复声明，避免参数不等价导致声明失败。
     */
    @TestConfiguration
    static class ContractConfig {

        @Bean
        public MessageCapture messageCapture() {
            return new MessageCapture();
        }
    }

    static class MessageCapture {

        @RabbitListener(queues = MessageQueueConsts.QUEUE_MEDIA_RESOURCE)
        public void capture(TranscodeMessage message) {
            RECEIVED.offer(message);
        }
    }

    @Test
    void transcodeMessageShouldSurviveRealBrokerRoundTrip() throws Exception {
        TranscodeMessage sent = TranscodeMessage.builder()
                .sourceUrl("http://magic-multimedia-minio:9000/magic-video/abc123.mp4")
                .videoId(10011L)
                .resourceKey("0a1b2c3d4e5f60718293a4b5c6d7e8f9")
                .build();

        messageProducer.sendTranscodeMessage(sent);

        TranscodeMessage received = RECEIVED.poll(30, TimeUnit.SECONDS);
        assertNotNull(received, "消息应能在 30 秒内被消费端收到（此前因可信包校验失败而永远收不到）");
        assertEquals(sent.getVideoId(), received.getVideoId());
        assertEquals(sent.getResourceKey(), received.getResourceKey());
        assertEquals(sent.getSourceUrl(), received.getSourceUrl());
    }
}
