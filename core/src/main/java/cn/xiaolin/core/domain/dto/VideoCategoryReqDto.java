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
public class VideoCategoryReqDto {
    private Long id;

    /**
     * 视频id
     */
    private Long videoId;

    /**
     * 视频类别id
     */
    private Long categoryId;
}
