package cn.xiaolin.core.controller;

import cn.xiaolin.utils.constant.ApiRouterConsts;
import cn.xiaolin.utils.resp.Result;
import cn.xiaolin.core.domain.dto.VideoReqDto;
import cn.xiaolin.core.domain.entity.Video;
import cn.xiaolin.core.service.VideoService;
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
@Tag(name = "视频详情", description = "视频详情增删改查基本操作")
@RestController
@RequestMapping(ApiRouterConsts.API_CORE_PREFIX)
@RequiredArgsConstructor
public class VideoController {
    private final VideoService videoService;

    @Operation(summary = "查询视频")
    @GetMapping("/video/{id}")
    public Result<Video> one(@PathVariable Long id) {
        Optional<Video> item = videoService.findItemById(id);
        return item.map(Result::ok).orElseGet(Result::notFound);
    }

    @Operation(summary = "新增视频")
    @PostMapping("/video")
    public Result<Video> insertOne(@RequestBody VideoReqDto dto) {
        Optional<Video> item = videoService.saveAndReturn(dto);
        return item.map(Result::ok).orElseGet(Result::badRequest);
    }

    @Operation(summary = "更新视频")
    @PutMapping("/video")
    public Result<Video> updateOne(@RequestBody VideoReqDto dto) {
        Optional<Video> item = videoService.updateAndReturn(dto);
        return item.map(Result::ok).orElseGet(Result::badRequest);
    }

    @Operation(summary = "删除视频")
    @DeleteMapping("/video/{id}")
    public Result<Video> deleteOne(@PathVariable Long id) {
        Optional<Video> item = videoService.deleteAndReturnById(id);
        return item.map(Result::ok).orElseGet(Result::badRequest);
    }
}
