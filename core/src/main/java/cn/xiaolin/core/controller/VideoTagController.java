package cn.xiaolin.core.controller;

import cn.xiaolin.core.domain.dto.VideoTagReqDto;
import cn.xiaolin.core.domain.entity.VideoTag;
import cn.xiaolin.core.service.VideoTagService;
import cn.xiaolin.utils.constant.ApiRouterConsts;
import cn.xiaolin.utils.resp.Result;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

/**
 * @author xingxiaolin xlxing@bupt.edu.cn
 * @Description
 * @create 2023/8/6
 */
@io.swagger.v3.oas.annotations.tags.Tag(name = "视频和标签的关联关系")
@RestController
@RequestMapping(ApiRouterConsts.API_CORE_PREFIX)
@RequiredArgsConstructor
public class VideoTagController {

    private final VideoTagService videoTagService;

    @Operation(summary = "查询视频和标签关联关系")
    @GetMapping("/video-tag/{id}")
    public Result<VideoTag> one(@PathVariable Long id) {
        Optional<VideoTag> item = videoTagService.findItemById(id);
        return item.map(Result::ok).orElseGet(Result::notFound);
    }

    @Operation(summary = "新增视频和标签关联关系")
    @PostMapping("/video-tag")
    public Result<VideoTag> insertOne(@RequestBody VideoTagReqDto dto) {
        Optional<VideoTag> item = videoTagService.saveAndReturn(dto);
        return item.map(Result::ok).orElseGet(Result::badRequest);
    }

    @Operation(summary = "更新视频和标签关联关系")
    @PutMapping("/video-tag")
    public Result<VideoTag> updateOne(@RequestBody VideoTagReqDto dto) {
        Optional<VideoTag> item = videoTagService.updateAndReturn(dto);
        return item.map(Result::ok).orElseGet(Result::badRequest);
    }

    @Operation(summary = "删除视频和标签关联关系")
    @DeleteMapping("/video-tag/{id}")
    public Result<VideoTag> deleteOne(@PathVariable Long id) {
        Optional<VideoTag> item = videoTagService.deleteAndReturnById(id);
        return item.map(Result::ok).orElseGet(Result::badRequest);
    }
}
