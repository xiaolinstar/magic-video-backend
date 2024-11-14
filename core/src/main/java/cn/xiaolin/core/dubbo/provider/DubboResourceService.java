package cn.xiaolin.core.dubbo.provider;

import cn.xiaolin.api.domain.dto.ResourceDto;
import cn.xiaolin.api.dubbo.service.ResourceService;
import org.apache.commons.lang3.NotImplementedException;
import org.apache.dubbo.config.annotation.DubboService;

/**
 * @author xingxiaolin xing.xiaolin@foxmail.com
 * @Description 获取resource，存储resource
 * @create 2024/11/11
 */
@DubboService
public class DubboResourceService implements ResourceService {

    @Override
    public ResourceDto getResourceById(Long id) {
        throw new NotImplementedException();
    }

    @Override
    public void saveResource(ResourceDto resourceDto) {
        throw new NotImplementedException();
    }
}
