package cn.xiaolin.multimedia.mq;

import com.rabbitmq.client.Channel;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueInformation;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * 死信队列路由测试。消费端 {@code basicNack(requeue=false)} 后，消息应落到死信队列。
 */
@SpringBootTest(
        classes = {
                RabbitMQConfig.class,
                MessageProducerImpl.class,
                DeadLetterRoutingTest.DlqConfig.class
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
class DeadLetterRoutingTest {

    /** 测试专用队列（带死信交换机参数） */
    private static final String TEST_QUEUE = "video.resource.dlq.routing.test";

    /** 死信队列中捕获到的消息 */
    private static final BlockingQueue<Message> DEAD = new ArrayBlockingQueue<>(10);

    @Autowired
    private MessageProducer messageProducer;

    @Autowired
    private RabbitAdmin rabbitAdmin;

    @TestConfiguration
    static class DlqConfig {

        @Bean
        public Queue routingTestQueue() {
            return new Queue(TEST_QUEUE, true, false, false,
                    Map.of("x-dead-letter-exchange", MessageQueueConsts.EXCHANGE_MEDIA_RESOURCE_DLQ));
        }

        @Bean
        public Binding routingTestBinding(Queue routingTestQueue) {
            return BindingBuilder.bind(routingTestQueue)
                    .to(new FanoutExchange(MessageQueueConsts.EXCHANGE_MEDIA_RESOURCE));
        }

        @Bean
        public NackCapture nackCapture() {
            return new NackCapture();
        }

        @Bean
        public DlqCapture dlqCapture() {
            return new DlqCapture();
        }
    }

    /**
     * 消费死信队列，捕获被 nack 的消息。
     */
    static class DlqCapture {

        @RabbitListener(queues = MessageQueueConsts.QUEUE_MEDIA_RESOURCE_DLQ)
        public void capture(Message message) {
            DEAD.offer(message);
        }
    }

    /**
     * 模拟消费失败：直接 nack 且不重投。
     */
    static class NackCapture {

        @RabbitListener(queues = TEST_QUEUE)
        public void capture(TranscodeMessage message, Message rawMessage, Channel channel) throws IOException {
            channel.basicNack(rawMessage.getMessageProperties().getDeliveryTag(), false, false);
        }
    }

    @Test
    void failedMessageShouldLandInDeadLetterQueue() throws Exception {
        rabbitAdmin.purgeQueue(MessageQueueConsts.QUEUE_MEDIA_RESOURCE_DLQ);

        TranscodeMessage sent = TranscodeMessage.builder()
                .sourceUrl("http://magic-minio:9000/raw/videos/fail.mp4")
                .videoId(10011L)
                .resourceKey("0a1b2c3d4e5f60718293a4b5c6d7e8f9")
                .build();

        messageProducer.sendTranscodeMessage(sent);

        Message dead = DEAD.poll(30, TimeUnit.SECONDS);
        assertNotNull(dead, "消费失败的消息应进入死信队列，而不是在原队列中无限重投");

        QueueInformation info = rabbitAdmin.getQueueInfo(TEST_QUEUE);
        assertEquals(0, info.getMessageCount(), "原队列不应残留消息");

        rabbitAdmin.deleteQueue(TEST_QUEUE);
    }
}
