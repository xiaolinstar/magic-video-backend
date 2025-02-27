package cn.xiaolin.message.service.consumer;

import cn.xiaolin.message.entity.ResourceMessage;

import java.net.URL;

/**
 * @author xingxiaolin xing.xiaolin@foxmail.com
 * @Description
 * @create 2024/11/13
 */
public interface MessageConsumer {
    void pullUrlMessage(URL url);
}
