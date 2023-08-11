package cn.xiaolin.core.controller;

import cn.xiaolin.core.service.VideoCategoryService;
import cn.xiaolin.utils.constant.ApiRouterConsts;
import cn.xiaolin.utils.resp.Result;
import cn.xiaolin.core.domain.dto.VideoCategoryReqDto;
import cn.xiaolin.core.domain.entity.VideoCategory;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

/**
 * @author xingxiaolin xlxing@bupt.edu.cn
 * @Description
 * @create 2023/7/30
 */
@Tag(name = "视频和类别的关联关系")
@RestController
@RequestMapping(ApiRouterConsts.API_CORE_PREFIX)
@RequiredArgsConstructor
public class VideoCategoryController {
    private final VideoCategoryService videoCategoryService;

    @Operation(summary = "查询视频和类别的关联关系")
    @GetMapping("/video-category/{id}")
    public Result<VideoCategory> one(@PathVariable Long id) {
        Optional<VideoCategory> item = videoCategoryService.findItemById(id);
        return item.map(Result::ok).orElseGet(Result::notFound);
    }

    @Operation(summary = "新增视频和类别的关联关系")
    @PostMapping("/video-category")
    public Result<VideoCategory> insertOne(@RequestBody VideoCategoryReqDto dto) {
        Optional<VideoCategory> item = videoCategoryService.saveAndReturn(dto);
        return item.map(Result::ok).orElseGet(Result::badRequest);
    }

    @Operation(summary = "更新视频和类别的关联关系")
    @PutMapping("/video-category")
    public Result<VideoCategory> updateOne(@RequestBody VideoCategoryReqDto dto) {
        Optional<VideoCategory> item = videoCategoryService.updateAndReturn(dto);
        return item.map(Result::ok).orElseGet(Result::badRequest);
    }

    @Operation(summary = "删除视频和类别的关联关系")
    @DeleteMapping("/video-category/{id}")
    public Result<VideoCategory> deleteOne(@PathVariable Long id) {
        Optional<VideoCategory> item = videoCategoryService.deleteAndReturnById(id);
        return item.map(Result::ok).orElseGet(Result::badRequest);
    }
}
