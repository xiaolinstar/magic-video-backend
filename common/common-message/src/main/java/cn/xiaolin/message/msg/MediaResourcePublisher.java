package cn.xiaolin.message.msg;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

/**
 * @author xingxiaolin xing.xiaolin@foxmail.com
 * @Description 发送Media资源上传上传消息
 * @create 2023/8/5
 */

@Component
@RequiredArgsConstructor
public class MediaResourcePublisher {
    private final RabbitTemplate rabbitTemplate;

    public void mediaUploadSuccess(String exchangeName, MediaUploadMsg msg) {
        rabbitTemplate.convertAndSend(exchangeName, "", msg);
    }

}
