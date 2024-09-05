package cn.xiaolin.core.service;

import cn.xiaolin.core.domain.dto.VideoReqDto;
import cn.xiaolin.core.domain.dto.VideoTagReqDto;
import cn.xiaolin.core.domain.entity.Video;
import cn.xiaolin.core.domain.entity.VideoTag;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Optional;

/**
 * @author xingxiaolin xing.xiaolin@foxmail.com
 * @Description 针对表【video_tag(视频标签关联关系表)】的数据库操作Service
 * @create 2023-07-30 17:10:49
 */
public interface VideoTagService extends IService<VideoTag> {
    Optional<VideoTag> findItemById(Long id);

    Optional<VideoTag> deleteAndReturnById(Long id);

    Optional<VideoTag> updateAndReturn(VideoTagReqDto dto);

    Optional<VideoTag> saveAndReturn(VideoTagReqDto dto);
}
