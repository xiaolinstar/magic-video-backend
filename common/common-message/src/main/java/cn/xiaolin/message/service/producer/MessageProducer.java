package cn.xiaolin.message.service.producer;

import cn.xiaolin.message.entity.ResourceMessage;

import java.net.URL;

/**
 * @author xingxiaolin xing.xiaolin@foxmail.com
 * @Description
 * @create 2024/11/11
 */
public interface MessageProducer {

    void publish(ResourceMessage resourceMessage);
    void publish(String exchange, ResourceMessage resourceMessage);
    void publish(String exchange, String routingKey, ResourceMessage resourceMessage);

    /**
     * 发送视频消息
     * @param url 视频地址
     */
    void sendVideoMessage(URL url);
}
