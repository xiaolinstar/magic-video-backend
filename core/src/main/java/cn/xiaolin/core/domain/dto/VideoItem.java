package cn.xiaolin.core.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author xingxiaolin xing.xiaolin@foxmail.com
 * @Description 视频资源接口
 * @create 2025/6/8
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class VideoItem {
    String type;
    Long id;
    Integer order;
    String title;
}
