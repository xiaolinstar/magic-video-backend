package cn.xiaolin.api.dubbo.service;

import cn.xiaolin.api.domain.dto.ResourceDto;


/**
 * @author xingxiaolin xing.xiaolin@foxmail.com
 * @Description core资源dubbo服务
 * @create 2024/10/13
 */
public interface ResourceService {

    ResourceDto getResourceById(Long id);


    void saveResource(ResourceDto resourceDto);
}
