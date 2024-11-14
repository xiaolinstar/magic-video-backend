package cn.xiaolin.message.service.producer;

import cn.xiaolin.message.entity.ResourceMessage;

/**
 * @author xingxiaolin xing.xiaolin@foxmail.com
 * @Description
 * @create 2024/11/11
 */
public interface ResourceProducer {

    void publish(ResourceMessage resourceMessage);
    void publish(String exchange, ResourceMessage resourceMessage);
    void publish(String exchange, String routingKey, ResourceMessage resourceMessage);
}
