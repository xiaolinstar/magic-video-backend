package cn.xiaolin.core.service;

import cn.xiaolin.core.domain.dto.VideoDirectorReqDto;
import cn.xiaolin.core.domain.entity.VideoDirector;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Optional;

/**
 * @author xingxiaolin xing.xiaolin@foxmail.com
 * @Description 针对表【video_director(视频导演)】的数据库操作Service
 * @create 2023-07-30 17:10:49
 */
public interface VideoDirectorService extends IService<VideoDirector> {
    Optional<VideoDirector> findItemById(Long id);

    Optional<VideoDirector> deleteAndReturnById(Long id);

    Optional<VideoDirector> updateAndReturn(VideoDirectorReqDto dto);

    Optional<VideoDirector> saveAndReturn(VideoDirectorReqDto dto);
}
