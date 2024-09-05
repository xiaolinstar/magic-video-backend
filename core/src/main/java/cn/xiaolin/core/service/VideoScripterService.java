package cn.xiaolin.core.service;

import cn.xiaolin.core.domain.dto.VideoMakerReqDto;
import cn.xiaolin.core.domain.dto.VideoScripterReqDto;
import cn.xiaolin.core.domain.entity.VideoMaker;
import cn.xiaolin.core.domain.entity.VideoScripter;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Optional;

/**
 * @author xingxiaolin xing.xiaolin@foxmail.com
 * @Description 针对表【video_scripter(视频编剧)】的数据库操作Service
 * @create 2023-07-30 17:10:49
 */
public interface VideoScripterService extends IService<VideoScripter> {
    Optional<VideoScripter> findItemById(Long id);

    Optional<VideoScripter> deleteAndReturnById(Long id);

    Optional<VideoScripter> updateAndReturn(VideoScripterReqDto dto);

    Optional<VideoScripter> saveAndReturn(VideoScripterReqDto dto);
}
