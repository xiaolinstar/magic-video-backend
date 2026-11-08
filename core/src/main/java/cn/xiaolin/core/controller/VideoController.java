package cn.xiaolin.core.controller;

import cn.xiaolin.core.domain.vo.VideoVO;
import cn.xiaolin.core.service.VideoService;
import cn.xiaolin.utils.constant.ApiRouterConsts;
import cn.xiaolin.utils.resp.PageQuery;
import cn.xiaolin.utils.resp.PageResult;
import cn.xiaolin.utils.resp.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author xingxiaolin xing.xiaolin@foxmail.com
 * @Description 视频详情控制器
 * @create 2023/7/30
 */
@Tag(name = "视频详情", description = "视频详情增删改查基本操作")
@RestController
@RequestMapping(ApiRouterConsts.API_CORE_PREFIX)
@RequiredArgsConstructor
public class VideoController {

    private final VideoService videoService;

    @Operation(summary = "分页查询视频资源")
    @GetMapping("/video/list")
    public Result<PageResult<VideoVO>> list(
            @RequestParam(defaultValue = "1") long current,
            @RequestParam(defaultValue = "20") long size,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false) String direction) {
        PageQuery query = PageQuery.builder()
                .current(current)
                .size(size)
                .sortBy(sortBy)
                .direction(direction)
                .build();
        return Result.ok(videoService.listVideoPage(query));
    }

    @Operation(summary = "根据 id 查询视频")
    @GetMapping("/video/{id}")
    public Result<VideoVO> one(@PathVariable Long id) {
        VideoVO videoVO = videoService.getVideoById(id);
        return videoVO == null ? Result.notFound() : Result.ok(videoVO);
    }

    /**
     * @deprecated 无分页的全量查询，仅为兼容存量调用方保留，后续改用 {@code GET /video/list}
     */
    @Deprecated
    @Operation(summary = "[已废弃] 查询所有视频，无分页，请使用 /video/list")
    @GetMapping("/video/all")
    public Result<List<VideoVO>> all() {
        List<VideoVO> videoList = videoService.getVideoList();
        return Result.ok(videoList);
    }
}
