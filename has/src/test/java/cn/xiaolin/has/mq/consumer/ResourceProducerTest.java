package cn.xiaolin.has.mq.consumer;

import cn.xiaolin.message.entity.ResourceMessage;
import cn.xiaolin.message.service.producer.MessageProducer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * @author xingxiaolin xing.xiaolin@foxmail.com
 * @Description
 * @create 2024/11/13
 */

@SpringBootTest
@ActiveProfiles("dev")
class ResourceProducerTest {

    @Autowired
    private MessageProducer resourceProducer;


    @Test
    public void publishResourceTest() {
        ResourceMessage resourceMessage = ResourceMessage.builder()
                .id(100L)
                .md5("message-md5")
                .mp4("message.mp4")
                .m3u8("message.m3u8")
                .build();
        resourceProducer.publish(resourceMessage);
    }


    @Test
    public void publishUrlTest() throws MalformedURLException {
        URL url = new URL("http://localhost:9010/magic-video/magic-video-test.mp4?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Credential=RzQywv1gIcgXeSSMMeNz%2F20250227%2Fus-east-1%2Fs3%2Faws4_request&X-Amz-Date=20250227T093410Z&X-Amz-Expires=604800&X-Amz-SignedHeaders=host&X-Amz-Signature=6336ba1d3e00a96d76fb0f4de05f4219382e2cc20011cd428a552bfd41d4566f");

        resourceProducer.sendVideoMessage(url);
    }

}