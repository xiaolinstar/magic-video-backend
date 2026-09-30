package cn.xiaolin.message.config;

import cn.xiaolin.message.constant.MessageQueueConsts;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * RabbitMQ configuration.
 *
 * @author xingxiaolin xing.xiaolin@foxmail.com
 * @create 2023/7/23
 */
@Configuration
public class RabbitMQConfig {

    /**
     * Trusted Java packages that may appear in the {@code __TypeId__} header.
     * Restricting this prevents attackers from pushing a payload that triggers
     * Jackson default-typing into arbitrary classes on the consumer side.
     */
    private static final String[] TRUSTED_PACKAGES = {
            "cn.xiaolin.message.entity",
            "cn.xiaolin.message.dto"
    };

    /**
     * Jackson-based message converter with restricted {@code __TypeId__} packages.
     * Default Spring behavior trusts every package, which is unsafe if the broker
     * is reachable from untrusted producers.
     *
     * @param objectMapper shared Jackson ObjectMapper bean
     * @return message converter
     */
    @Bean
    public MessageConverter messageConverter(ObjectMapper objectMapper) {
        Jackson2JsonMessageConverter converter = new Jackson2JsonMessageConverter(objectMapper);
        converter.setTrustedPackages(TRUSTED_PACKAGES);
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
     * @return 消息队列
     */
    @Bean("resourceQueue")
    public Queue resourceFanoutQueue() {
        return new Queue(MessageQueueConsts.QUEUE_MEDIA_RESOURCE);
    }

    /**
     * 绑定交换机和队列
     * @param queue 消息队列
     * @param fanoutExchange 消息队列交换机
     * @return 绑定关系
     */
    @Bean
    public Binding resourceFanoutBinding(@Qualifier("resourceQueue")Queue queue, FanoutExchange fanoutExchange) {
        return BindingBuilder.bind(queue).to(fanoutExchange);
    }
}