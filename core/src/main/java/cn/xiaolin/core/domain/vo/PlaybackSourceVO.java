package cn.xiaolin.core.domain.vo;

import cn.xiaolin.core.domain.entity.VideoSource;
import lombok.*;

import java.util.List;

/**
 * @author xingxiaolin xing.xiaolin@foxmail.com
 * @Description
 * @create 2025/6/8
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class PlaybackSourceVO {
    private Long videoId;
    private List<VideoSource> sources;
}
