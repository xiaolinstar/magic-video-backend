package cn.xiaolin.core.controller;

import cn.xiaolin.core.service.VideoActorService;
import cn.xiaolin.utils.constant.ApiRouterConsts;
import cn.xiaolin.utils.resp.Result;
import cn.xiaolin.core.domain.dto.VideoActorReqDto;
import cn.xiaolin.core.domain.entity.VideoActor;
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
@Tag(name = "视频和演员的关联关系")
@RestController
@RequestMapping(ApiRouterConsts.API_CORE_PREFIX)
@RequiredArgsConstructor
public class VideoActorController {
    private final VideoActorService videoActorService;

    @Operation(summary = "查询视频演员关联关系")
    @GetMapping("/video-actor/{id}")
    public Result<VideoActor> one(@PathVariable Long id) {
        Optional<VideoActor> item = videoActorService.findItemById(id);
        return item.map(Result::ok).orElseGet(Result::notFound);
    }

    @Operation(summary = "新增视频演员关联关系")
    @PostMapping("/video-actor")
    public Result<VideoActor> insertOne(@RequestBody VideoActorReqDto dto) {
        Optional<VideoActor> item = videoActorService.saveAndReturn(dto);
        return item.map(Result::ok).orElseGet(Result::badRequest);
    }

    @Operation(summary = "更新视频演员关联关系")
    @PutMapping("/video-actor")
    public Result<VideoActor> updateOne(@RequestBody VideoActorReqDto dto) {
        Optional<VideoActor> item = videoActorService.updateAndReturn(dto);
        return item.map(Result::ok).orElseGet(Result::badRequest);
    }

    @Operation(summary = "删除视频演员关联关系")
    @DeleteMapping("/video-actor/{id}")
    public Result<VideoActor> deleteOne(@PathVariable Long id) {
        Optional<VideoActor> item = videoActorService.deleteAndReturnById(id);
        return item.map(Result::ok).orElseGet(Result::badRequest);
    }
}
