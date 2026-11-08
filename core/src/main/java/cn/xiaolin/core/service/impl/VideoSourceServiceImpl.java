package cn.xiaolin.core.service.impl;

import cn.xiaolin.core.domain.entity.VideoSource;
import cn.xiaolin.core.domain.mapper.VideoSourceMapper;
import cn.xiaolin.core.domain.vo.PlaybackSourceVO;
import cn.xiaolin.core.service.VideoSourceService;
import cn.xiaolin.utils.resp.PageQuery;
import cn.xiaolin.utils.resp.PageResult;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author xingxiaolin xing.xiaolin@foxmail.com
 * @Description 针对表【resource(视频资源)】的数据库操作Service实现
 * @create 2023-07-30 17:10:49
 */
@Service
@RequiredArgsConstructor
public class VideoSourceServiceImpl extends ServiceImpl<VideoSourceMapper, VideoSource>
    implements VideoSourceService {

    @Override
    public PageResult<PlaybackSourceVO> listSourcePage(PageQuery pageQuery) {
        long current = pageQuery.getCurrent();
        long size = pageQuery.getSize();

        // 第一步：以 videoId 为单位去重后分页，保证 total / pages 与返回条目数口径一致。
        // 排序：仅 latest 有底层字段支撑（videoId 为雪花 ID，倒序即最新优先）；
        // banner / classic / featured 等缺少可用于排序的字段，暂未实现，统一走默认顺序。
        LambdaQueryWrapper<VideoSource> idWrapper = new LambdaQueryWrapper<VideoSource>()
                .select(VideoSource::getVideoId)
                .groupBy(VideoSource::getVideoId)
                .orderByDesc(VideoSource::getVideoId);
        Page<VideoSource> idPage = this.page(new Page<>(current, size), idWrapper);

        List<Long> videoIds = idPage.getRecords() == null
                ? Collections.emptyList()
                : idPage.getRecords().stream().map(VideoSource::getVideoId).toList();

        if (videoIds.isEmpty()) {
            return PageResult.of(idPage.getCurrent(), idPage.getSize(), idPage.getTotal(),
                    Collections.emptyList());
        }

        // 第二步：按当前页的 videoId 批量取出全部资源行，再按分页顺序聚合
        List<VideoSource> rows = this.list(new LambdaQueryWrapper<VideoSource>()
                .in(VideoSource::getVideoId, videoIds)
                .orderByAsc(VideoSource::getId));

        Map<Long, List<VideoSource>> grouped = new LinkedHashMap<>();
        for (Long videoId : videoIds) {
            grouped.put(videoId, new ArrayList<>());
        }
        for (VideoSource vs : rows) {
            grouped.computeIfAbsent(vs.getVideoId(), k -> new ArrayList<>()).add(vs);
        }

        List<PlaybackSourceVO> mapped = new ArrayList<>(grouped.size());
        grouped.forEach((videoId, sources) -> mapped.add(new PlaybackSourceVO(videoId, sources)));

        return PageResult.of(idPage.getCurrent(), idPage.getSize(), idPage.getTotal(), mapped);
    }

    @Override
    @Deprecated
    public List<PlaybackSourceVO> getPlaybackSourceList() {
        // 存量接口：保持原有语义，对全表资源按 videoId 做全局聚合
        List<VideoSource> rows = this.list();
        Map<Long, List<VideoSource>> grouped = new LinkedHashMap<>();
        for (VideoSource vs : rows) {
            grouped.computeIfAbsent(vs.getVideoId(), k -> new ArrayList<>()).add(vs);
        }
        return grouped.entrySet().stream()
                .map(e -> new PlaybackSourceVO(e.getKey(), e.getValue()))
                .collect(Collectors.toList());
    }
}