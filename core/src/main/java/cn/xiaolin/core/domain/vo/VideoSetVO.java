package cn.xiaolin.core.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author xingxiaolin xing.xiaolin@foxmail.com
 * @Description 视频数据结构集合
 * @create 2025/6/10
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
public class VideoSetVO {

    private List<CollectionVO> collections;
    private List<SeasonVO> seasons;
    private List<VideoVO> videos;
    private List<PlaybackSourceVO> playbackSources;
}
