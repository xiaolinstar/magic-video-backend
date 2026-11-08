package cn.xiaolin.core.service.impl;

import cn.xiaolin.core.domain.entity.Video;
import cn.xiaolin.core.domain.mapper.VideoMapper;
import cn.xiaolin.core.domain.vo.VideoVO;
import cn.xiaolin.core.service.VideoService;
import cn.xiaolin.utils.exception.GlobalException;
import cn.xiaolin.utils.resp.PageQuery;
import cn.xiaolin.utils.resp.PageResult;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author xingxiaolin xing.xiaolin@foxmail.com
 * @Description 针对表【video(视频)】的数据库操作Service实现
 * @create 2023-07-30 17:10:49
 */
@Service
@RequiredArgsConstructor
public class VideoServiceImpl extends ServiceImpl<VideoMapper, Video>
    implements VideoService {

    /** 单条视频查询的缓存名称，key 为视频 id，过期时间沿用 CacheConfig 中的全局配置 */
    private static final String CACHE_VIDEO = "video";

    @Override
    @Deprecated
    public List<VideoVO> getVideoList() {
        // 保留给存量调用方（CollectionServiceImpl）使用，新代码请改用 listVideoPage(PageQuery)。
        // 该方法仍然是每次调用全表扫描。
        List<Video> videoList = this.list();
        return videoList.stream()
                .map(this::toVideoVO)
                .collect(Collectors.toList());
    }

    @Override
    @Cacheable(value = CACHE_VIDEO, key = "#id")
    public VideoVO getVideoById(Long id) {
        Video video = this.getById(id);
        // 记录不存在时直接返回 null，由 disableCachingNullValues 保证空值不会写入缓存
        if (Objects.isNull(video)) {
            return null;
        }
        return toVideoVO(video);
    }

    @Override
    public PageResult<VideoVO> listVideoPage(PageQuery pageQuery) {
        long current = pageQuery.getCurrent();
        long size = pageQuery.getSize();

        LambdaQueryWrapper<Video> wrapper = new LambdaQueryWrapper<>();
        applySort(pageQuery.getSortBy(), pageQuery.getDirection(), wrapper);

        Page<Video> paged = this.page(new Page<>(current, size), wrapper);
        List<VideoVO> mapped = paged.getRecords().stream()
                .map(this::toVideoVO)
                .collect(Collectors.toList());

        return PageResult.of(paged.getCurrent(), paged.getSize(), paged.getTotal(), mapped);
    }

    /**
     * 把排序键转换为 MyBatis-Plus 的排序条件。
     * 不支持的排序键抛出 {@link GlobalException}，避免静默返回未排序结果。
     */
    private void applySort(String sortBy, String direction, LambdaQueryWrapper<Video> wrapper) {
        String key = sortBy == null ? "" : sortBy.toLowerCase(Locale.ROOT);
        boolean asc = direction != null && direction.trim().equalsIgnoreCase("asc");
        switch (key) {
            case "rating" -> wrapper.orderBy(true, asc, Video::getRating);
            case "duration" -> wrapper.orderBy(true, asc, Video::getDuration);
            case "release", "release_date" -> wrapper.orderBy(true, asc, Video::getReleaseDate);
            case "latest", "" -> wrapper.orderBy(true, false, Video::getCreateTime);
            default -> throw new GlobalException("不支持的 sortBy: " + sortBy);
        }
    }

    /**
     * 把数据库实体转换为对外 VO，并按视频类型区分所属集合/季
     */
    private VideoVO toVideoVO(Video video) {
        VideoVO videoVO = new VideoVO(video);

        if (Objects.equals(video.getType(), "episode")) {
            videoVO.setSeasonId(video.getParentId());
        } else {
            videoVO.setCollectionId(video.getParentId());
        }
        videoVO.setVideoNumber(video.getSortOrder());
        return videoVO;
    }
}