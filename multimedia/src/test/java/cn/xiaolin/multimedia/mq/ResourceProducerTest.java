package cn.xiaolin.multimedia.mq;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * 手工向开发环境 broker 投递消息。需要已启动的 RabbitMQ 与 dev profile。
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
    public void publishUrlTest() {
        TranscodeMessage message = TranscodeMessage.builder()
                .sourceUrl("http://localhost:9010/magic-video/magic-video-test.mp4")
                .videoId(10011L)
                .resourceKey("0a1b2c3d4e5f60718293a4b5c6d7e8f9")
                .build();

        resourceProducer.sendTranscodeMessage(message);
    }
}
