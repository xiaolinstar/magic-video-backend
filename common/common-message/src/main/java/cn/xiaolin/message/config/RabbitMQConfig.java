package cn.xiaolin.message.config;

import cn.xiaolin.message.constant.MessageQueueConsts;
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
 * @author xingxiaolin xing.xiaolin@foxmail.com
 * @Description 消息队列配置
 * @create 2023/7/23
 */
@Configuration
public class RabbitMQConfig {

    /**
     * RabbitMQ 消息转换器，使用Jackson消息转换器
     * @return Jackson消息转换器
     */
    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    /**
     * fanout交换机
     * @return 具有广播功能的消息队列交换机
     */
    @Bean
    public FanoutExchange fanoutExchange() {
        return new FanoutExchange(MessageQueueConsts.EXCHANGE_MEDIA_RESOURCE);
    }

    @Bean("resourceQueue")
    public Queue resourceFanoutQueue() {
        return new Queue(MessageQueueConsts.QUEUE_MEDIA_RESOURCE);
    }

    @Bean
    public Binding resourceFanoutBinding(@Qualifier("resourceQueue")Queue queue, FanoutExchange fanoutExchange) {
        return BindingBuilder.bind(queue).to(fanoutExchange);
    }
}
