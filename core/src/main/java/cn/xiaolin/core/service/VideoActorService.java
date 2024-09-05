package cn.xiaolin.core.service;

import cn.xiaolin.core.domain.dto.TagReqDto;
import cn.xiaolin.core.domain.dto.VideoActorReqDto;
import cn.xiaolin.core.domain.entity.Tag;
import cn.xiaolin.core.domain.entity.VideoActor;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Optional;

/**
 * @author xingxiaolin xing.xiaolin@foxmail.com
 * @Description 针对表【video_actor(视频演员)】的数据库操作Service
 * @create 2023-07-30 17:10:49
 */
public interface VideoActorService extends IService<VideoActor> {
    Optional<VideoActor> findItemById(Long id);

    Optional<VideoActor> deleteAndReturnById(Long id);

    Optional<VideoActor> updateAndReturn(VideoActorReqDto dto);

    Optional<VideoActor> saveAndReturn(VideoActorReqDto dto);
}
