package cn.xiaolin.has.mq;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.support.converter.DefaultJackson2JavaTypeMapper;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 消费侧 Rabbit 拓扑与 JSON 转换器。队列参数与 multimedia 侧声明一致，任一服务都可以单独把拓扑建起来。
 */
@Slf4j
@Configuration
public class RabbitMQConfig {

    /**
     * {@code __TypeId__} 允许的包名。id 映射未覆盖的类型仍要过白名单。
     * {@code cn.xiaolin.message.entity} 与 {@code cn.xiaolin.multimedia.mq} 用于读取历史消息和生产端当前类名。
     */
    private static final String[] TRUSTED_PACKAGES = {
            "cn.xiaolin.has.mq",
            "cn.xiaolin.multimedia.mq",
            "cn.xiaolin.message.entity"
    };

    /**
     * 监听方法的具体参数类型优先于 {@code __TypeId__}，因此可以反序列化到本服务的 {@link TranscodeMessage}。
     * 映射表同时接受历史类名和生产端类名。同一类的最后一项是写出时使用的 id。
     */
    @Bean
    public MessageConverter messageConverter(ObjectMapper objectMapper) {
        Jackson2JsonMessageConverter converter = new Jackson2JsonMessageConverter(objectMapper, TRUSTED_PACKAGES);
        DefaultJackson2JavaTypeMapper typeMapper = new DefaultJackson2JavaTypeMapper();
        typeMapper.setTrustedPackages(TRUSTED_PACKAGES);
        Map<String, Class<?>> ids = new LinkedHashMap<>();
        ids.put("cn.xiaolin.message.entity.TranscodeMessage", TranscodeMessage.class);
        ids.put("cn.xiaolin.multimedia.mq.TranscodeMessage", TranscodeMessage.class);
        ids.put(TranscodeMessage.class.getName(), TranscodeMessage.class);
        typeMapper.setIdClassMapping(ids);
        converter.setJavaTypeMapper(typeMapper);
        return converter;
    }

    @Bean
    public FanoutExchange fanoutExchange() {
        return new FanoutExchange(MessageQueueConsts.EXCHANGE_MEDIA_RESOURCE);
    }

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

    @Bean
    public Binding resourceFanoutBinding(@Qualifier("resourceQueue") Queue queue, FanoutExchange fanoutExchange) {
        return BindingBuilder.bind(queue).to(fanoutExchange);
    }

    @Bean
    public FanoutExchange deadLetterExchange() {
        return new FanoutExchange(MessageQueueConsts.EXCHANGE_MEDIA_RESOURCE_DLQ);
    }

    @Bean("resourceDlq")
    public Queue resourceDeadLetterQueue() {
        return new Queue(MessageQueueConsts.QUEUE_MEDIA_RESOURCE_DLQ);
    }

    @Bean
    public Binding resourceDeadLetterBinding(@Qualifier("resourceDlq") Queue queue,
                                             FanoutExchange deadLetterExchange) {
        return BindingBuilder.bind(queue).to(deadLetterExchange);
    }

    /**
     * 声明队列失败且原因是 {@code PRECONDITION_FAILED} 时，删除旧的 {@code video.resource} 后重建。
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
