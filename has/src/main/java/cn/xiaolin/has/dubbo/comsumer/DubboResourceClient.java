package cn.xiaolin.has.dubbo.comsumer;

import cn.xiaolin.api.domain.dto.ResourceDto;
import cn.xiaolin.api.dubbo.service.ResourceService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Service;

/**
 * @author xingxiaolin xing.xiaolin@foxmail.com
 * @Description rcp调用core微服务的写resource服务
 * @create 2024/10/13
 */
@Service
public class DubboResourceClient {

    @DubboReference
    private ResourceService resourceService;

    public ResourceDto getResourceById(Long id) {
        return resourceService.getResourceById(id);
    }

    public void saveResource(ResourceDto resourceDto) {
        resourceService.saveResource(resourceDto);
    }


}
