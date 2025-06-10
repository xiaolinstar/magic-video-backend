package cn.xiaolin.core.service.impl;

import cn.xiaolin.core.domain.entity.Season;
import cn.xiaolin.core.domain.entity.Video;
import cn.xiaolin.core.domain.vo.SeasonVO;
import cn.xiaolin.core.service.VideoService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.xiaolin.core.service.SeasonService;
import cn.xiaolin.core.domain.mapper.SeasonMapper;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
* @author xingxiaolin
*/
@Service
@RequiredArgsConstructor
public class SeasonServiceImpl extends ServiceImpl<SeasonMapper, Season>
    implements SeasonService{

    private final VideoService videoService;

    @Override
    public List<SeasonVO> getSeasonList() {
        List<Season> seasonList = this.list();
        List<SeasonVO> seasonVOList = seasonList
                .stream()
                .map(SeasonVO::new)
                .toList();

        List<Video> videoList = videoService.list();

        // seasonId -> episodeList
        Map<Long, List<Video>> episodeListMap = videoList.stream()
                .filter(video -> Objects.equals(video.getType(), "episode"))
                .collect(Collectors.groupingBy(Video::getParentId));

        for (SeasonVO seasonVO : seasonVOList) {
            Long seasonId = seasonVO.getId();
            List<Video> episodeList = episodeListMap.get(seasonId);
            episodeList.sort(Comparator.comparingInt(Video::getSortOrder));

            List<SeasonVO.Episode> episodes = getEpisodes(episodeList);

            seasonVO.setEpisodes(episodes);
        }

        return seasonVOList;
    }

    @Override
    public Optional<SeasonVO> getSeasonById(Long id) {
        Season season = this.getById(id);
        if (season == null) {
            return Optional.empty();
        } else {
            LambdaQueryWrapper<Video> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Video::getType, "episode")
                    .eq(Video::getParentId, id);
            List<Video> episodeList = videoService.list(wrapper);
            episodeList.sort(Comparator.comparingInt(Video::getSortOrder));

            List<SeasonVO.Episode> episodes = getEpisodes(episodeList);

            SeasonVO vo = new SeasonVO(season);
            vo.setEpisodes(episodes);
            return Optional.of(vo);
        }
    }

    @NotNull
    private static List<SeasonVO.Episode> getEpisodes(List<Video> episodeList) {
        List<SeasonVO.Episode> episodes = new ArrayList<>();

        for (Video video : episodeList) {
            SeasonVO.Episode episode = new SeasonVO.Episode();

            episode.setId(video.getId());
            episode.setSeasonId(video.getParentId());
            episode.setEpisodeNumber(video.getSortOrder());
            episode.setTitle(video.getTitle());
            episode.setDuration(video.getDuration());

            episodes.add(episode);
        }
        return episodes;
    }
}




