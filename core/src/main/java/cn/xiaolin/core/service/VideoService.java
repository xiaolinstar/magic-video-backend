package cn.xiaolin.core.service;

import cn.xiaolin.core.domain.entity.Video;
import cn.xiaolin.core.domain.vo.VideoVO;
import cn.xiaolin.utils.resp.PageQuery;
import cn.xiaolin.utils.resp.PageResult;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @author xingxiaolin xing.xiaolin@foxmail.com
 * @Description 针对表【video(视频)】的数据库操作Service
 * @create 2023-07-30 17:10:49
 */
public interface VideoService extends IService<Video> {

    /**
     * @deprecated 无分页的全表查询，请改用 {@link #listVideoPage(PageQuery)}
     */
    @Deprecated
    List<VideoVO> getVideoList();

    /**
     * 根据 id 查询视频，结果带缓存；视频不存在时返回 null
     */
    VideoVO getVideoById(Long id);

    /**
     * 分页查询视频。{@link PageQuery#getSortBy()} 支持 rating、duration、release、latest，
     * 为空时默认按创建时间倒序
     */
    PageResult<VideoVO> listVideoPage(PageQuery pageQuery);
}