package cn.xiaolin.core.domain.vo;

import cn.xiaolin.core.domain.entity.Video;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author xingxiaolin xing.xiaolin@foxmail.com
 * @Description video episode clip 视频单元
 * @create 2025/6/9
 */
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Data
public class VideoVO extends Video {

    private Long collectionId; // movie clip
    private Long seasonId; // season
    private Integer seasonNumber; // TODO 季号，可通过 seasonId 查询，冗余参数
    private Integer videoNumber; // 视频在集合中的序号，season 中别名 episodeNumber

    private List<String> genres; // 分类标签
    private List<String> cast; // 演员阵容
    private List<String> directors; // 导演
    private List<String> tags; // 标签

    public VideoVO(Video video) {
        this.setId(video.getId());
        this.setTitle(video.getTitle());
        this.setOriginalTitle(video.getOriginalTitle());
        this.setCoverImage(video.getCoverImage());
        this.setType(video.getType());
        this.setReleaseDate(video.getReleaseDate());
        this.setDuration(video.getDuration());
        this.setRating(video.getRating());
    }

    public Integer getEpisodeNumber() {
        return videoNumber;
    }


}
