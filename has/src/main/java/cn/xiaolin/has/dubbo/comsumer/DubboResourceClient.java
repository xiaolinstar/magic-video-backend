package cn.xiaolin.has.dubbo.comsumer;

import cn.xiaolin.api.domain.dto.ResourceDto;
import cn.xiaolin.api.dubbo.service.ResourceService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Service;

/**
 * rcp调用core微服务的写resource服务
 *
 * <p>直连模式：不使用注册中心，地址由 {@code dubbo.core.host} / {@code dubbo.core.port} 指定
 * （各 profile 的 bootstrap 文件里按部署环境给出默认值）。
 * 原因见 {@code docs/adr/0008-remove-nacos.md}。
 *
 * @author xingxiaolin xing.xiaolin@foxmail.com
 * @Description rcp调用core微服务的写resource服务
 * @create 2024/10/13
 */
@Service
public class DubboResourceClient {

    @DubboReference(url = "dubbo://${dubbo.core.host}:${dubbo.core.port}", check = false)
    private ResourceService resourceService;

    public ResourceDto getResourceById(Long id) {
        return resourceService.getResourceById(id);
    }

    public void saveResource(ResourceDto resourceDto) {
        resourceService.saveResource(resourceDto);
    }
}
