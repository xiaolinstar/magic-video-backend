package cn.xiaolin.multimedia.mq;

import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * 转码消息契约的真实 broker 往返测试。
 *
 * <p>只加载 MQ 配置，避免拉起 MinIO。连接参数与原 common-message 测试一致。
 */
@SpringBootTest(
        classes = {
                RabbitMQConfig.class,
                MessageProducerImpl.class,
                TranscodeMessageContractTest.ContractConfig.class
        },
        webEnvironment = SpringBootTest.WebEnvironment.NONE,
        properties = {
                "spring.rabbitmq.host=localhost",
                "spring.rabbitmq.port=5672",
                "spring.rabbitmq.username=${RABBITMQ_DEFAULT_USER:xiaolin}",
                "spring.rabbitmq.password=${RABBITMQ_DEFAULT_PASS:123456rabbit}",
                "spring.rabbitmq.virtual-host=${RABBITMQ_DEFAULT_VHOST:/dev-media}",
                "spring.rabbitmq.connection-timeout=15000"
        }
)
class TranscodeMessageContractTest {

    /** 捕获到的消息 */
    private static final BlockingQueue<TranscodeMessage> RECEIVED = new ArrayBlockingQueue<>(10);

    @Autowired
    private MessageProducer messageProducer;

    /**
     * 监听器单独放成 Bean：{@code @SpringBootTest} 不会把测试类本身注册为 Bean，
     * 而 {@code @RabbitListener} 需要容器能发现它。
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
