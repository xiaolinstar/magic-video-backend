package cn.xiaolin.core.controller;

import cn.xiaolin.core.domain.vo.PlaybackSourceVO;
import cn.xiaolin.core.service.VideoSourceService;
import cn.xiaolin.utils.constant.ApiRouterConsts;
import cn.xiaolin.utils.resp.PageQuery;
import cn.xiaolin.utils.resp.PageResult;
import cn.xiaolin.utils.resp.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 视频资源控制器
 *
 * <p>下列列表接口统一接收 {@code current} + {@code size}，返回 {@link PageResult}，
 * 不再返回无分页的全表数据。
 *
 * @author xingxiaolin xing.xiaolin@foxmail.com
 * @Description 视频资源控制器
 * @create 2023/7/30
 */
@Tag(name = "视频资源")
@RestController
@RequestMapping(ApiRouterConsts.API_CORE_PREFIX)
@RequiredArgsConstructor
@Slf4j
public class ResourceController {

    private final VideoSourceService videoSourceService;

    @Operation(summary = "分页查询所有视频资源")
    @GetMapping("/resource/all")
    public Result<PageResult<PlaybackSourceVO>> all(
            @RequestParam(defaultValue = "1") long current,
            @RequestParam(defaultValue = "20") long size) {
        return Result.ok(videoSourceService.listSourcePage(pageQuery(current, size, null)));
    }

    @Operation(summary = "推荐视频资源（分页，推荐排序暂未实现）")
    @GetMapping("/resource/recommend")
    public Result<PageResult<PlaybackSourceVO>> recommend(
            @RequestParam(defaultValue = "1") long current,
            @RequestParam(defaultValue = "20") long size) {
        // 推荐排序缺少支撑字段，暂未实现，当前按 videoId 倒序返回
        return Result.ok(videoSourceService.listSourcePage(pageQuery(current, size, null)));
    }

    @Operation(summary = "轮播视频资源（分页，轮播筛选暂未实现）")
    @GetMapping("/resource/banner")
    public Result<PageResult<PlaybackSourceVO>> banner(
            @RequestParam(defaultValue = "1") long current,
            @RequestParam(defaultValue = "20") long size) {
        // 轮播标记缺少支撑字段，暂未实现，当前按 videoId 倒序返回
        return Result.ok(videoSourceService.listSourcePage(pageQuery(current, size, null)));
    }

    @Operation(summary = "最新视频资源（分页，按 videoId 倒序）")
    @GetMapping("/resource/latest")
    public Result<PageResult<PlaybackSourceVO>> latest(
            @RequestParam(defaultValue = "1") long current,
            @RequestParam(defaultValue = "20") long size) {
        return Result.ok(videoSourceService.listSourcePage(pageQuery(current, size, "latest")));
    }

    @Operation(summary = "经典视频资源（分页，经典排序暂未实现）")
    @GetMapping("/resource/classic")
    public Result<PageResult<PlaybackSourceVO>> classic(
            @RequestParam(defaultValue = "1") long current,
            @RequestParam(defaultValue = "20") long size) {
        // 经典度缺少支撑字段，暂未实现，当前按 videoId 倒序返回
        return Result.ok(videoSourceService.listSourcePage(pageQuery(current, size, null)));
    }

    @Operation(summary = "精选视频资源（分页，精选排序暂未实现）")
    @GetMapping("/resource/featured")
    public Result<PageResult<PlaybackSourceVO>> featured(
            @RequestParam(defaultValue = "1") long current,
            @RequestParam(defaultValue = "20") long size) {
        log.info("获取精选视频 /resource/featured");
        // 精选标记缺少支撑字段，暂未实现，当前按 videoId 倒序返回
        return Result.ok(videoSourceService.listSourcePage(pageQuery(current, size, null)));
    }

    private PageQuery pageQuery(long current, long size, String sortBy) {
        return PageQuery.builder()
                .current(current)
                .size(size)
                .sortBy(sortBy)
                .direction("desc")
                .build();
    }
}
