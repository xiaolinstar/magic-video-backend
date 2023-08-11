package cn.xiaolin.core.message;
import cn.xiaolin.core.domain.dto.ResourceReqDto;
import cn.xiaolin.core.domain.entity.Resource;
import cn.xiaolin.core.service.ResourceService;
import cn.xiaolin.message.constant.MessageQueueConsts;
import cn.xiaolin.message.msg.MediaUploadMsg;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.context.annotation.Configuration;

import java.util.Optional;

/**
 * @author xingxiaolin xlxing@bupt.edu.cn
 * @Description RabbitMQ监听消息队列
 * @create 2023/8/5
 */

@Configuration
@RequiredArgsConstructor
@Slf4j
public class MessageListener {
    private final ResourceService resourceService;

    /**
     * 监听视频资源上传消息，存储数据库
     * @param dto 视频资源
     */
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = MessageQueueConsts.QUEUE_MEDIA_RESOURCE),
            exchange = @Exchange(name = MessageQueueConsts.EXCHANGE_MEDIA_RESOURCE, type = ExchangeTypes.FANOUT)
    ))
    public void listenDirectExchange(MediaUploadMsg dto) {
        ResourceReqDto resourceReqDto = new ResourceReqDto();
        resourceReqDto.setId(dto.getId());
        resourceReqDto.setRawFilePath(dto.getRawFilePath());
        resourceReqDto.setName(dto.getName());
        resourceReqDto.setMpd(dto.getMpd());
        resourceReqDto.setM3u8(dto.getM3u8());
        resourceReqDto.setMd5(dto.getMd5());
        Optional<Resource> resource = resourceService.saveAndReturn(resourceReqDto);
        resource.ifPresent(r -> log.info(r.toString()));
    }

}
