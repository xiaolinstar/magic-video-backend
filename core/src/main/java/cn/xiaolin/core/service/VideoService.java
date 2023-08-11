package cn.xiaolin.core.service;

import cn.xiaolin.core.domain.dto.VideoReqDto;
import cn.xiaolin.core.domain.entity.Video;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Optional;

/**
* @author xlxing
* @description 针对表【video(视频)】的数据库操作Service
* @createDate 2023-07-30 17:10:49
*/
public interface VideoService extends IService<Video> {
    Optional<Video> findItemById(Long id);

    Optional<Video> deleteAndReturnById(Long id);

    Optional<Video> updateAndReturn(VideoReqDto dto);

    Optional<Video> saveAndReturn(VideoReqDto dto);
}
