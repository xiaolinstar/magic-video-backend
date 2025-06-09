package cn.xiaolin.core.service;

import cn.xiaolin.core.domain.entity.Video;
import cn.xiaolin.core.domain.vo.VideoVO;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Optional;

/**
 * @author xingxiaolin xing.xiaolin@foxmail.com
 * @Description 针对表【video(视频)】的数据库操作Service
 * @create 2023-07-30 17:10:49
 */
public interface VideoService extends IService<Video> {

    List<VideoVO> getVideoList();
}
