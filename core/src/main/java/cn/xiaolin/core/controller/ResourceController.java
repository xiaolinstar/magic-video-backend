package cn.xiaolin.core.controller;

import cn.xiaolin.utils.constant.ApiRouterConsts;
import cn.xiaolin.utils.resp.Result;
import cn.xiaolin.core.domain.dto.ResourceReqDto;
import cn.xiaolin.core.domain.entity.Resource;
import cn.xiaolin.core.service.ResourceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

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

    private final ResourceService resourceService;

    @Operation(summary = "查询资源")
    @GetMapping("/resource/{id}")
    public Result<Resource> one(@PathVariable Long id) {
        Optional<Resource> item = resourceService.findItemById(id);
        return item.map(Result::ok).orElseGet(Result::notFound);
    }

    @Operation(summary = "新增资源")
    @PostMapping("/resource")
    public Result<Resource> insertOne(@RequestBody ResourceReqDto dto) {
        Optional<Resource> item = resourceService.saveAndReturn(dto);
        return item.map(Result::ok).orElseGet(Result::badRequest);
    }

    @Operation(summary = "更新资源")
    @PutMapping("/resource")
    public Result<Resource> updateOne(@RequestBody ResourceReqDto dto) {
        Optional<Resource> item = resourceService.updateAndReturn(dto);
        return item.map(Result::ok).orElseGet(Result::badRequest);
    }

    @Operation(summary = "删除资源")
    @DeleteMapping("/resource/{id}")
    public Result<Resource> deleteOne(@PathVariable Long id) {
        Optional<Resource> item = resourceService.deleteAndReturnById(id);
        return item.map(Result::ok).orElseGet(Result::badRequest);
    }

    @Operation(summary = "获取所有视频资源")
    @GetMapping("/resource/all")
    public Result<List<Resource>> listResource() {
        List<Resource> resources = resourceService.list();
        return Result.ok(resources);
    }

    // TODO 后续完善方法实现

    @Operation(summary = "获取推荐视频资源")
    @GetMapping("/resource/recommend")
    public Result<List<Resource>> recommendResource() {
        List<Resource> resources = resourceService.list();
        return Result.ok(resources);
    }

    @Operation(summary = "获取轮播视频资源")
    @GetMapping("/resource/banner")
    public Result<List<Resource>> bannerResource() {
        List<Resource> resources = resourceService.list();
        return Result.ok(resources);
    }

    @Operation(summary = "获取最新视频资源")
    @GetMapping("/resource/latest")
    public Result<List<Resource>> latestResource() {
        List<Resource> resources = resourceService.list();
        return Result.ok(resources);
    }

    @Operation(summary = "获取经典视频资源")
    @GetMapping("/resource/classic")
    public Result<List<Resource>> classicResource() {
        List<Resource> resources = resourceService.list();
        return Result.ok(resources);
    }

    @Operation(summary = "获取精选视频")
    @GetMapping("/resource/featured")
    public Result<List<Resource>> featuredResource() {
        log.info("获取精选视频 /resource/featured");
        log.debug("<Debug> /resource/featured");
        List<Resource> resources = resourceService.list();
        return Result.ok(resources);
    }
}
