package cn.xiaolin.core.domain.dto;

import lombok.*;

/**
 * @author xingxiaolin xlxing@bupt.edu.cn
 * @Description
 * @create 2023/7/30
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class VideoTagReqDto {
    private Long id;

    /**
     * 视频id
     */
    private Long videoId;

    /**
     * 标签id
     */
    private Long tagId;
}
