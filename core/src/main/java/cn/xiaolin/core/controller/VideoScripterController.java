package cn.xiaolin.core.controller;

import cn.xiaolin.core.domain.dto.VideoScripterReqDto;
import cn.xiaolin.core.domain.entity.VideoScripter;
import cn.xiaolin.core.service.VideoScripterService;
import cn.xiaolin.utils.constant.ApiRouterConsts;
import cn.xiaolin.utils.resp.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

/**
 * @author xingxiaolin xlxing@bupt.edu.cn
 * @Description
 * @create 2023/8/6
 */

@Tag(name = "视频和编剧的关联关系")
@RestController
@RequiredArgsConstructor
@RequestMapping(ApiRouterConsts.API_CORE_PREFIX)
public class VideoScripterController {
    private final VideoScripterService videoScripterService;

    @Operation(summary = "查询视频制作人关联关系")
    @GetMapping("/video-scripter/{id}")
    public Result<VideoScripter> one(@PathVariable Long id) {
        Optional<VideoScripter> item = videoScripterService.findItemById(id);
        return item.map(Result::ok).orElseGet(Result::notFound);
    }

    @Operation(summary = "新增视频制作人关联关系")
    @PostMapping("/video-scripter")
    public Result<VideoScripter> insertOne(@RequestBody VideoScripterReqDto dto) {
        Optional<VideoScripter> item = videoScripterService.saveAndReturn(dto);
        return item.map(Result::ok).orElseGet(Result::badRequest);
    }

    @Operation(summary = "更新视频制作人关联关系")
    @PutMapping("/video-scripter")
    public Result<VideoScripter> updateOne(@RequestBody VideoScripterReqDto dto) {
        Optional<VideoScripter> item = videoScripterService.updateAndReturn(dto);
        return item.map(Result::ok).orElseGet(Result::badRequest);
    }

    @Operation(summary = "删除视频制作人关联关系")
    @DeleteMapping("/video-scripter/{id}")
    public Result<VideoScripter> deleteOne(@PathVariable Long id) {
        Optional<VideoScripter> item = videoScripterService.deleteAndReturnById(id);
        return item.map(Result::ok).orElseGet(Result::badRequest);
    }
}
