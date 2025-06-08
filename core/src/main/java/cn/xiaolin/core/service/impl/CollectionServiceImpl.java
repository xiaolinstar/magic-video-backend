package cn.xiaolin.core.service.impl;

import cn.xiaolin.core.domain.dto.VideoItem;
import cn.xiaolin.core.domain.entity.Collection;
import cn.xiaolin.core.domain.entity.Season;
import cn.xiaolin.core.domain.entity.Video;
import cn.xiaolin.core.domain.mapper.CollectionMapper;
import cn.xiaolin.core.domain.vo.CollectionVO;
import cn.xiaolin.core.service.CollectionService;
import cn.xiaolin.core.service.SeasonService;
import cn.xiaolin.core.service.VideoService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

/**
* @author xingxiaolin
*/
@Service
@RequiredArgsConstructor
public class CollectionServiceImpl extends ServiceImpl<CollectionMapper, Collection>
    implements CollectionService {

    private final VideoService videoService;
    private final SeasonService seasonService;

    @Override
    public List<CollectionVO> getCollectionList() {
        List<Collection> collectionList = this.list();

        //  从 VideoService 中查询 parentId 集合
        List<Video> videoList = videoService.list();

        List<Season> seasonList = seasonService.list();

        Map<Long, List<VideoItem>> videoIdMap = new HashMap<>();

        for (Season season : seasonList) {
            Long parentId = season.getCollectionId();
            if (!videoIdMap.containsKey(parentId)) {
                videoIdMap.put(parentId, new ArrayList<>());
            }
            List<VideoItem> items = videoIdMap.get(parentId);
            items.add(VideoItem.builder()
                    .type("season")
                    .id(season.getId())
                    .order(season.getSeasonNumber())
                    .build());
        }

        for (Video video: videoList) {
            Long parentId = video.getParentId();

            if (!Objects.equals(video.getType(), "episode")) {
                if (!videoIdMap.containsKey(parentId)) {
                    videoIdMap.put(parentId, new ArrayList<>());
                }
                List<VideoItem> items = videoIdMap.get(parentId);
                items.add(VideoItem.builder()
                        .type(video.getType())
                        .id(video.getId())
                        .order(video.getVideoOrder())
                        .build());
            }

        }

        List<CollectionVO> collectionVOList = new ArrayList<>();
        for (Collection collection : collectionList) {
            CollectionVO collectionVO = new CollectionVO(collection);
            collectionVO.setItems(videoIdMap.get(collection.getId()));
            collectionVO.setRelatedCollections(List.of());
            collectionVO.setReleaseYear(2025);
            collectionVOList.add(collectionVO);
        }

        return collectionVOList;
    }
}




