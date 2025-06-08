package cn.xiaolin.core.domain.vo;

import cn.xiaolin.core.domain.entity.Season;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author xingxiaolin xing.xiaolin@foxmail.com
 * @Description
 * @create 2025/6/8
 */
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Data
public class SeasonVO extends Season {
    private List<Episode> episodes;

    public SeasonVO(Season season) {
        this.setId(season.getId());
        this.setCollectionId(season.getCollectionId());
        this.setSeasonNumber(season.getSeasonNumber());
        this.setTitle(season.getTitle());
        this.setDescription(season.getDescription());
        this.setCoverImage(season.getCoverImage());
        this.setReleaseDate(season.getReleaseDate());
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    public static class Episode {
        private Long id;
        private Long seasonId;
        private Integer episodeNumber;
        private String title;
        private Integer duration;
        private String plot;
    }
}
