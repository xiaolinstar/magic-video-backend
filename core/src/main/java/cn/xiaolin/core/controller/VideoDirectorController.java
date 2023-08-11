package cn.xiaolin.core.controller;

import cn.xiaolin.core.domain.dto.VideoDirectorReqDto;
import cn.xiaolin.core.domain.entity.VideoDirector;
import cn.xiaolin.core.service.VideoDirectorService;
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
@Tag(name = "视频和导演的关联关系")
@RestController
@RequiredArgsConstructor
@RequestMapping(ApiRouterConsts.API_CORE_PREFIX)
public class VideoDirectorController {
    private final VideoDirectorService videoDirectorService;



    @Operation(summary = "查询视频导演关联关系")
    @GetMapping("/video-director/{id}")
    public Result<VideoDirector> one(@PathVariable Long id) {
        Optional<VideoDirector> item = videoDirectorService.findItemById(id);
        return item.map(Result::ok).orElseGet(Result::notFound);
    }

    @Operation(summary = "新增视频导演关联关系")
    @PostMapping("/video-director")
    public Result<VideoDirector> insertOne(@RequestBody VideoDirectorReqDto dto) {
        Optional<VideoDirector> item = videoDirectorService.saveAndReturn(dto);
        return item.map(Result::ok).orElseGet(Result::badRequest);
    }

    @Operation(summary = "更新视频导演关联关系")
    @PutMapping("/video-director")
    public Result<VideoDirector> updateOne(@RequestBody VideoDirectorReqDto dto) {
        Optional<VideoDirector> item = videoDirectorService.updateAndReturn(dto);
        return item.map(Result::ok).orElseGet(Result::badRequest);
    }

    @Operation(summary = "删除视频导演关联关系")
    @DeleteMapping("/video-director/{id}")
    public Result<VideoDirector> deleteOne(@PathVariable Long id) {
        Optional<VideoDirector> item = videoDirectorService.deleteAndReturnById(id);
        return item.map(Result::ok).orElseGet(Result::badRequest);
    }
}
