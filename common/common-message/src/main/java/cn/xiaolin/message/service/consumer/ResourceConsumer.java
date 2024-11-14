package cn.xiaolin.message.service.consumer;

import cn.xiaolin.message.entity.ResourceMessage;

/**
 * @author xingxiaolin xing.xiaolin@foxmail.com
 * @Description
 * @create 2024/11/13
 */
public interface ResourceConsumer {
    void consume(ResourceMessage resourceMessage);
}
