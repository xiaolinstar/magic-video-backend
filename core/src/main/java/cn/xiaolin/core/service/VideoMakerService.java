package cn.xiaolin.core.service;

import cn.xiaolin.core.domain.dto.VideoMakerReqDto;
import cn.xiaolin.core.domain.entity.VideoMaker;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Optional;

/**
 * @author xingxiaolin xing.xiaolin@foxmail.com
 * @Description 针对表【video_maker(视频制作人)】的数据库操作Service
 * @create 2023-07-30 17:10:49
 */
public interface VideoMakerService extends IService<VideoMaker> {
    Optional<VideoMaker> findItemById(Long id);

    Optional<VideoMaker> deleteAndReturnById(Long id);

    Optional<VideoMaker> updateAndReturn(VideoMakerReqDto dto);

    Optional<VideoMaker> saveAndReturn(VideoMakerReqDto dto);
}
