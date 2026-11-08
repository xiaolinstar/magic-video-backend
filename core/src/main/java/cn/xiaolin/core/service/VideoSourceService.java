package cn.xiaolin.core.service;

import cn.xiaolin.core.domain.entity.VideoSource;
import cn.xiaolin.core.domain.vo.PlaybackSourceVO;
import cn.xiaolin.utils.resp.PageQuery;
import cn.xiaolin.utils.resp.PageResult;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @author xingxiaolin xing.xiaolin@foxmail.com
 * @Description 针对表【resource(视频资源)】的数据库操作Service
 * @create 2023-07-30 17:10:49
 */
public interface VideoSourceService extends IService<VideoSource> {

    /**
     * 分页查询播放资源，同一页内的资源按 videoId 聚合成 {@link PlaybackSourceVO}。
     *
     * <p>分页口径以 videoId 为单位（先按 videoId 去重再分页），因此 total、返回条目数与
     * pages 三者语义一致，不会出现重复页或空页。
     */
    PageResult<PlaybackSourceVO> listSourcePage(PageQuery pageQuery);

    /**
     * @deprecated 无分页的全表扫描，请改用 {@link #listSourcePage(PageQuery)}。
     * 仅为需要全量资源图的 {@code CollectionService.getVideoSet} 保留。
     */
    @Deprecated
    List<PlaybackSourceVO> getPlaybackSourceList();
}