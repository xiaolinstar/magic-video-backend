package cn.xiaolin.core.service;

import cn.xiaolin.core.domain.entity.VideoSource;
import cn.xiaolin.core.domain.vo.PlaybackSourceVO;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @author xingxiaolin xing.xiaolin@foxmail.com
 * @Description 针对表【resource(视频资源)】的数据库操作Service
 * @create 2023-07-30 17:10:49
 */
public interface VideoSourceService extends IService<VideoSource> {
    List<PlaybackSourceVO> getPlaybackSourceList();
}
