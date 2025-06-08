package cn.xiaolin.core.controller;

import cn.xiaolin.core.domain.entity.VideoSource;
import cn.xiaolin.utils.constant.ApiRouterConsts;
import cn.xiaolin.utils.resp.Result;
import cn.xiaolin.core.service.VideoSourceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.NotImplementedException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
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

    private final VideoSourceService resourceService;

    @Operation(summary = "查询资源")
    @GetMapping("/resource/{id}")
    public Result<VideoSource> one(@PathVariable Long id) {
        throw new NotImplementedException();
    }

    @Operation(summary = "新增资源")
    @PostMapping("/resource")
    public Result<VideoSource> insertOne() {
        throw new NotImplementedException();
    }

    @Operation(summary = "更新资源")
    @PutMapping("/resource")
    public Result<VideoSource> updateOne() {
        throw new NotImplementedException();
    }

    @Operation(summary = "删除资源")
    @DeleteMapping("/resource/{id}")
    public Result<VideoSource> deleteOne(@PathVariable Long id) {
        throw new NotImplementedException();
    }

    @Operation(summary = "获取所有视频资源")
    @GetMapping("/resource/all")
    public Result<List<VideoSource>> listResource() {
        List<VideoSource> videoSources = resourceService.list();
        return Result.ok(videoSources);
    }

    // TODO 后续完善方法实现

    @Operation(summary = "获取推荐视频资源")
    @GetMapping("/resource/recommend")
    public Result<List<VideoSource>> recommendResource() {
        List<VideoSource> videoSources = resourceService.list();
        return Result.ok(videoSources);
    }

    @Operation(summary = "获取轮播视频资源")
    @GetMapping("/resource/banner")
    public Result<List<VideoSource>> bannerResource() {
        List<VideoSource> videoSources = resourceService.list();
        return Result.ok(videoSources);
    }

    @Operation(summary = "获取最新视频资源")
    @GetMapping("/resource/latest")
    public Result<List<VideoSource>> latestResource() {
        List<VideoSource> videoSources = resourceService.list();
        return Result.ok(videoSources);
    }

    @Operation(summary = "获取经典视频资源")
    @GetMapping("/resource/classic")
    public Result<List<VideoSource>> classicResource() {
        List<VideoSource> videoSources = resourceService.list();
        return Result.ok(videoSources);
    }

    @Operation(summary = "获取精选视频")
    @GetMapping("/resource/featured")
    public Result<List<VideoSource>> featuredResource() {
        log.info("获取精选视频 /resource/featured");
        log.debug("<Debug> /resource/featured");
        List<VideoSource> videoSources = resourceService.list();
        return Result.ok(videoSources);
    }
}
