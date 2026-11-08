package cn.xiaolin.message;

import cn.xiaolin.message.constant.MessageQueueConsts;
import cn.xiaolin.message.entity.TranscodeMessage;
import cn.xiaolin.message.service.producer.MessageProducer;
import com.rabbitmq.client.Channel;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
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

import static org.junit.jupiter.api.Assertions.*;

/**
 * 死信队列路由测试。
 *
 * <p>此前 {@code @RabbitListener} 使用默认 auto-ack，且队列没有配置死信交换机：
 * 消费失败的消息要么被丢弃、要么在原队列中无限重投（毒消息循环），失败任务无处可查。
 *
 * <p>本测试用真实 RabbitMQ 验证：消费端 {@code basicNack(requeue=false)} 之后，
 * 消息会被投递到死信交换机并最终落到死信队列，而不是留在原队列里空转。
 */
@SpringBootTest
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
        // 清空死信队列，避免上一轮残留干扰
        rabbitAdmin.purgeQueue(MessageQueueConsts.QUEUE_MEDIA_RESOURCE_DLQ);

        TranscodeMessage sent = TranscodeMessage.builder()
                .sourceUrl("http://magic-multimedia-minio:9000/magic-video/fail.mp4")
                .videoId(10011L)
                .resourceKey("0a1b2c3d4e5f60718293a4b5c6d7e8f9")
                .build();

        messageProducer.sendTranscodeMessage(sent);

        // 死信队列里应能收到这条消息
        Message dead = DEAD.poll(30, TimeUnit.SECONDS);
        assertNotNull(dead, "消费失败的消息应进入死信队列，而不是在原队列中无限重投");

        // 原队列不应残留消息（否则就是毒消息循环）
        QueueInformation info = rabbitAdmin.getQueueInfo(TEST_QUEUE);
        assertEquals(0, info.getMessageCount(), "原队列不应残留消息");

        rabbitAdmin.deleteQueue(TEST_QUEUE);
    }
}
