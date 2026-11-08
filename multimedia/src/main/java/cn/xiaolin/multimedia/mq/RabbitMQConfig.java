package cn.xiaolin.multimedia.mq;

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
 * RabbitMQ 拓扑与 JSON 转换器。has 侧有一份同名声明，队列参数必须保持一致。
 */
@Slf4j
@Configuration
public class RabbitMQConfig {

    /**
     * {@code __TypeId__} 允许的包名。未登记在 id 映射里的类型仍要过这道白名单，
     * 避免 broker 上的消息被反序列化成任意类。
     *
     * <p>{@code cn.xiaolin.message.entity} 是拆分 common-message 之前的 DTO 包，保留以便读取历史消息。
     */
    private static final String[] TRUSTED_PACKAGES = {
            "cn.xiaolin.multimedia.mq",
            "cn.xiaolin.message.entity"
    };

    /**
     * 基于 Jackson 的消息转换器。
     *
     * <p>监听方法带具体参数类型时，Spring AMQP 优先用该类型反序列化，因此 has 可以使用自己的 DTO。
     * id 映射保证历史 {@code __TypeId__}（{@code cn.xiaolin.message.entity.TranscodeMessage}）
     * 以及当前两端类名都能落到本服务的类型上。映射表里同一类的最后一项是写出时使用的 id。
     */
    @Bean
    public MessageConverter messageConverter(ObjectMapper objectMapper) {
        Jackson2JsonMessageConverter converter = new Jackson2JsonMessageConverter(objectMapper, TRUSTED_PACKAGES);
        DefaultJackson2JavaTypeMapper typeMapper = new DefaultJackson2JavaTypeMapper();
        typeMapper.setTrustedPackages(TRUSTED_PACKAGES);
        Map<String, Class<?>> ids = new LinkedHashMap<>();
        ids.put("cn.xiaolin.message.entity.TranscodeMessage", TranscodeMessage.class);
        ids.put("cn.xiaolin.has.mq.TranscodeMessage", TranscodeMessage.class);
        ids.put(TranscodeMessage.class.getName(), TranscodeMessage.class);
        ids.put("cn.xiaolin.message.entity.ResourceMessage", ResourceMessage.class);
        ids.put(ResourceMessage.class.getName(), ResourceMessage.class);
        typeMapper.setIdClassMapping(ids);
        converter.setJavaTypeMapper(typeMapper);
        return converter;
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
     * <p>只有明确是 {@code PRECONDITION_FAILED} 才会触发删除；其他异常原样抛出。
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
