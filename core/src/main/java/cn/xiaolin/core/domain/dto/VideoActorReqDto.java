package cn.xiaolin.core.domain.dto;

import cn.xiaolin.core.domain.entity.VideoActor;
import lombok.*;

/**
 * @author xingxiaolin xing.xiaolin@foxmail.com
 * @Description
 * @create 2023/7/30
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
public class VideoActorReqDto {
    private Long id;
    /**
     * 视频id
     */
    private Long videoId;

    /**
     * 演员id
     */
    private Long actorId;
}
