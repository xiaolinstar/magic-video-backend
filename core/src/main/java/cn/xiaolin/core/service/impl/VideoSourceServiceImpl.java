package cn.xiaolin.core.service.impl;

import cn.xiaolin.core.domain.entity.VideoSource;
import cn.xiaolin.core.domain.mapper.VideoSourceMapper;
import cn.xiaolin.core.domain.vo.PlaybackSourceVO;
import cn.xiaolin.core.service.VideoSourceService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author xingxiaolin xing.xiaolin@foxmail.com
 * @Description 针对表【resource(视频资源)】的数据库操作Service实现
 * @create 2023-07-30 17:10:49
 */
@Service
@RequiredArgsConstructor
public class VideoSourceServiceImpl extends ServiceImpl<VideoSourceMapper, VideoSource>
    implements VideoSourceService {


    @Override
    public List<PlaybackSourceVO> getPlaybackSourceList() {
        List<VideoSource> videoSourceList = this.list();
        Map<Long, List<VideoSource>> videoSourceListMap = videoSourceList.stream()
                .collect(Collectors.groupingBy(VideoSource::getVideoId));

        return videoSourceListMap.entrySet()
                .stream()
                .map((e) -> new PlaybackSourceVO(e.getKey(), e.getValue()))
                .toList();
    }
}





