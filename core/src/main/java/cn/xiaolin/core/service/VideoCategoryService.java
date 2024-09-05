package cn.xiaolin.core.service;

import cn.xiaolin.core.domain.dto.VideoActorReqDto;
import cn.xiaolin.core.domain.dto.VideoCategoryReqDto;
import cn.xiaolin.core.domain.entity.VideoActor;
import cn.xiaolin.core.domain.entity.VideoCategory;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Optional;

/**
 * @author xingxiaolin xing.xiaolin@foxmail.com
 * @Description 针对表【video_category(视频分类关联关系表)】的数据库操作Service
 * @create 2023-07-30 17:10:49
 */
public interface VideoCategoryService extends IService<VideoCategory> {
    Optional<VideoCategory> findItemById(Long id);

    Optional<VideoCategory> deleteAndReturnById(Long id);

    Optional<VideoCategory> updateAndReturn(VideoCategoryReqDto dto);

    Optional<VideoCategory> saveAndReturn(VideoCategoryReqDto dto);
}
