package cn.xiaolin.core.controller;

import cn.xiaolin.utils.constant.ApiRouterConsts;
import cn.xiaolin.utils.resp.Result;
import cn.xiaolin.core.domain.dto.ResourceReqDto;
import cn.xiaolin.core.domain.entity.Resource;
import cn.xiaolin.core.service.ResourceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * @author xingxiaolin xlxing@bupt.edu.cn
 * @Description
 * @create 2023/7/30
 */
@Tag(name = "视频资源")
@RestController
@RequestMapping(ApiRouterConsts.API_CORE_PREFIX)
@RequiredArgsConstructor
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
        List<Resource> resources = resourceService.listResource();
        return Result.ok(resources);
    }

}
