package cn.xiaolin.core.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author xingxiaolin xlxing@bupt.edu.cn
 * @Description
 * @create 2023/7/30
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
public class VideoScripterReqDto {
    private Long id;

    /**
     * 视频id
     */
    private Long videoId;

    /**
     * 编剧id
     */
    private Long scripterId;
}
