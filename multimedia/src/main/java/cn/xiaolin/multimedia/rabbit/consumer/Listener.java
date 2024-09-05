package cn.xiaolin.multimedia.rabbit.consumer;

import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

/**
 * @author xingxiaolin xing.xiaolin@foxmail.com
 * @Description Rabbit监听器
 * @create 2023/7/29
 */

@Configuration
public class Listener {

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "direct.queue1"),
            exchange = @Exchange(name = "xiaolin.direct", type = ExchangeTypes.DIRECT),
            key = {"red", "blue"}
    ))
    public void listenDirectExchange(Map<String, Object> msg) {
        System.out.println(msg);
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue("topic.queue1"),
            exchange = @Exchange(name = "xiaolin.topic", type = ExchangeTypes.TOPIC),
            key = "#.beijing"
    ))
    public void listenTopicExchange(String msg) {
        System.out.println(msg);
    }
}
