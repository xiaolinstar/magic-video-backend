package cn.xiaolin.core.domain.dto;

import lombok.*;

/**
 * @author xingxiaolin xing.xiaolin@foxmail.com
 * @Description
 * @create 2023/7/30
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class VideoDirectorReqDto {
    private Long id;

    /**
     * 视频id
     */
    private Long videoId;

    /**
     * 导演id
     */
    private Long directorId;
}
