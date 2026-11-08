package cn.xiaolin.message.config;

import cn.xiaolin.message.constant.MessageQueueConsts;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

import lombok.extern.slf4j.Slf4j;

/**
 * RabbitMQ configuration.
 *
 * @author xingxiaolin xing.xiaolin@foxmail.com
 * @create 2023/7/23
 */
@Slf4j
@Configuration
public class RabbitMQConfig {

    /**
     * {@code __TypeId__} 消息头中允许出现的受信包名。
     * 限制包名可避免攻击者推送能被 Jackson 反序列化成任意类的消息体。
     *
     * <p>说明：Spring AMQP 3.x 已移除该信任列表的 setter，只能在
     * {@link Jackson2JsonMessageConverter} 的构造器（可变参数）中传入。
     *
     * <p><b>注意</b>：只有本包（及其子包）中的类型可以出现在消息体里。
     * 曾经用 {@code java.net.URL} 直接作为消息体，消费端会因可信包校验失败而
     * 抛出 {@code IllegalArgumentException}，消息永远无法被消费。
     */
    private static final String[] TRUSTED_PACKAGES = {
            "cn.xiaolin.message.entity",
            "cn.xiaolin.message.dto"
    };

    /**
     * 基于 Jackson 的消息转换器，并限制 {@code __TypeId__} 允许的包名。
     * Spring 默认信任所有包，若 broker 可被不可信的生产者访问则存在风险。
     *
     * @param objectMapper 共享的 Jackson ObjectMapper Bean
     * @return 消息转换器
     */
    @Bean
    public MessageConverter messageConverter(ObjectMapper objectMapper) {
        return new Jackson2JsonMessageConverter(objectMapper, TRUSTED_PACKAGES);
    }

    /**
     * fanout交换机
     * @return 具有广播功能的消息队列交换机
     */
    @Bean
    public FanoutExchange fanoutExchange() {
        return new FanoutExchange(MessageQueueConsts.EXCHANGE_MEDIA_RESOURCE);
    }

    /**
     * 消息队列
     *
     * <p>队列声明了死信交换机：消费失败并 {@code nack(requeue=false)} 的消息会被
     * 转发到死信队列，而不是在原队列中无限重投。
     *
     * @return 具有广播功能的消息队列
     */
    @Bean("resourceQueue")
    public Queue resourceFanoutQueue() {
        return new Queue(
                MessageQueueConsts.QUEUE_MEDIA_RESOURCE,
                true,
                false,
                false,
                Map.of("x-dead-letter-exchange", MessageQueueConsts.EXCHANGE_MEDIA_RESOURCE_DLQ)
        );
    }

    /**
     * 绑定交换机和队列
     * @param queue 消息队列
     * @param fanoutExchange 消息队列交换机
     * @return 绑定关系
     */
    @Bean
    public Binding resourceFanoutBinding(@Qualifier("resourceQueue") Queue queue, FanoutExchange fanoutExchange) {
        return BindingBuilder.bind(queue).to(fanoutExchange);
    }

    /**
     * 死信交换机：接收转码失败的消息
     */
    @Bean
    public FanoutExchange deadLetterExchange() {
        return new FanoutExchange(MessageQueueConsts.EXCHANGE_MEDIA_RESOURCE_DLQ);
    }

    /**
     * 死信队列：转码失败消息的最终去处，需人工介入排查
     */
    @Bean("resourceDlq")
    public Queue resourceDeadLetterQueue() {
        return new Queue(MessageQueueConsts.QUEUE_MEDIA_RESOURCE_DLQ);
    }

    /**
     * 死信队列绑定
     */
    @Bean
    public Binding resourceDeadLetterBinding(@Qualifier("resourceDlq") Queue queue,
                                             FanoutExchange deadLetterExchange) {
        return BindingBuilder.bind(queue).to(deadLetterExchange);
    }

    /**
     * 自定义 RabbitAdmin：声明队列失败时，若原因是历史队列参数不等价，则删除旧队列后重建。
     *
     * <p><b>为什么需要</b>：死信交换机是本次新增的队列参数。
     * RabbitMQ 不允许在参数不等价时重新声明同名队列，已有环境中的
     * {@code video.resource} 队列因为缺少 {@code x-dead-letter-exchange}，
     * 重新声明会返回 {@code PRECONDITION_FAILED}，导致服务启动失败。
     *
     * <p><b>为什么可以直接删</b>：历史队列里残留的是 {@code java.net.URL} 类型的消息，
     * 消费端因 Jackson 可信包校验失败永远无法反序列化，属于不可消费的毒消息，丢弃无损失。
     *
     * <p>只有明确是 {@code PRECONDITION_FAILED} 才会触发删除；其他异常原样抛出，
     * 避免误删已有正常队列。
     */
    @Bean
    public RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory) {
        return new RabbitAdmin(connectionFactory) {
            @Override
            public void initialize() {
                try {
                    super.initialize();
                } catch (AmqpException e) {
                    if (!isPreconditionFailure(e)) {
                        throw e;
                    }
                    log.warn("队列声明失败（历史队列参数不等价），将删除旧队列后重建：{}", e.getMessage());
                    if (deleteQueueIfExists(connectionFactory, MessageQueueConsts.QUEUE_MEDIA_RESOURCE)) {
                        super.initialize();
                    } else {
                        throw e;
                    }
                }
            }
        };
    }

    /**
     * 判断异常链中是否包含 {@code PRECONDITION_FAILED}。
     */
    private boolean isPreconditionFailure(Throwable throwable) {
        Throwable current = throwable;
        while (current != null) {
            String message = current.getMessage();
            if (message != null && message.contains("PRECONDITION_FAILED")) {
                return true;
            }
            current = current.getCause();
        }
        return false;
    }

    /**
     * 删除指定队列。
     */
    private boolean deleteQueueIfExists(ConnectionFactory connectionFactory, String queueName) {
        org.springframework.amqp.rabbit.connection.Connection connection = null;
        Channel channel = null;
        try {
            connection = connectionFactory.createConnection();
            channel = connection.createChannel(false);
            channel.queueDelete(queueName);
            log.info("已删除历史队列 {}（缺少死信交换机参数），将按新配置重新声明", queueName);
            return true;
        } catch (Exception e) {
            log.warn("删除历史队列 {} 失败：{}", queueName, e.getMessage());
            return false;
        } finally {
            try {
                if (channel != null) {
                    channel.close();
                }
                if (connection != null) {
                    connection.close();
                }
            } catch (Exception ignored) {
                // 关闭异常无需处理
            }
        }
    }
}
