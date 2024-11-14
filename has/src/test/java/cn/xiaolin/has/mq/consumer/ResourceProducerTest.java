package cn.xiaolin.has.mq.consumer;

import cn.xiaolin.message.entity.ResourceMessage;
import cn.xiaolin.message.service.producer.ResourceProducer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * @author xingxiaolin xing.xiaolin@foxmail.com
 * @Description
 * @create 2024/11/13
 */

@SpringBootTest
@ActiveProfiles("dev")
class ResourceProducerTest {

    @Autowired
    private ResourceProducer resourceProducer;


    @Test
    public void publishTest() {
        ResourceMessage resourceMessage = ResourceMessage.builder()
                .id(100L)
                .md5("message-md5")
                .mp4("message.mp4")
                .m3u8("message.m3u8")
                .build();
        resourceProducer.publish(resourceMessage);
    }

}