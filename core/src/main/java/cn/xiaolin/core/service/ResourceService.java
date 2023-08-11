package cn.xiaolin.core.service;

import cn.xiaolin.core.domain.dto.ResourceReqDto;
import cn.xiaolin.core.domain.entity.Resource;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Optional;

/**
* @author xlxing
* @description 针对表【resource(视频资源)】的数据库操作Service
* @createDate 2023-07-30 17:10:49
*/
public interface ResourceService extends IService<Resource> {
    Optional<Resource> findItemById(Long id);

    Optional<Resource> deleteAndReturnById(Long id);

    Optional<Resource> updateAndReturn(ResourceReqDto dto);

    Optional<Resource> saveAndReturn(ResourceReqDto dto);
}
