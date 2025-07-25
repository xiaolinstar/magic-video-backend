package cn.xiaolin.core.service.impl;

import cn.xiaolin.core.domain.vo.SlideVO;
import cn.xiaolin.core.domain.vo.VideoVO;
import cn.xiaolin.core.service.VideoService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.xiaolin.core.domain.entity.Video;
import cn.xiaolin.core.domain.mapper.VideoMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * @author xingxiaolin xing.xiaolin@foxmail.com
 * @Description 针对表【video(视频)】的数据库操作Service实现
 * @create 2023-07-30 17:10:49
 */
@Service
@RequiredArgsConstructor
public class VideoServiceImpl extends ServiceImpl<VideoMapper, Video>
    implements VideoService {

    @Override
    public List<VideoVO> getVideoList() {
        List<Video> videoList = this.list();
        List<VideoVO> videoVOList = new ArrayList<>();
        for (Video video : videoList) {
            VideoVO videoVO = getVideoVO(video);
            videoVOList.add(videoVO);
        }
        return videoVOList;
    }

    @Override
    public Optional<VideoVO> getVideoById(Long id) {
        Video video = this.getById(id);
        if (Objects.isNull(video)) {
            return Optional.empty();
        } else {
            VideoVO videoVO = getVideoVO(video);
            return Optional.of(videoVO);
        }
    }

    private VideoVO getVideoVO(Video video) {
        VideoVO videoVO = new VideoVO(video);

        if (Objects.equals(video.getType(), "episode")) {
            videoVO.setSeasonId(video.getParentId());
        } else {
            videoVO.setCollectionId(video.getParentId());
        }
        videoVO.setVideoNumber(video.getSortOrder());
        return videoVO;
    }
}




