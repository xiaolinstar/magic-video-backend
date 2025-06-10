package cn.xiaolin.core.domain.vo;

import cn.xiaolin.core.domain.entity.Video;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * @author xingxiaolin xing.xiaolin@foxmail.com
 * @Description 视频轮播图
 * @create 2025/6/10
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
public class SlideVO {
    private Long id;
    private String title;
    private String description;
    private String coverImage;

    public SlideVO(Video video) {
        this.id = video.getId();
        this.title = video.getTitle();
        this.description = video.getDescription();
        this.coverImage = video.getCoverImage();
    }
}
