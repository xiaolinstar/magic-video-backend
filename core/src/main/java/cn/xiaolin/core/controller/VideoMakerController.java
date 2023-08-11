package cn.xiaolin.core.controller;

import cn.xiaolin.core.domain.dto.VideoMakerReqDto;
import cn.xiaolin.core.domain.dto.VideoReqDto;
import cn.xiaolin.core.domain.entity.Video;
import cn.xiaolin.core.domain.entity.VideoMaker;
import cn.xiaolin.core.service.VideoMakerService;
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
@Tag(name = "视频和制作人的关联关系，制作人是演员编剧和导演的统称")
@RestController
@RequiredArgsConstructor
@RequestMapping(ApiRouterConsts.API_CORE_PREFIX)
public class VideoMakerController {
    private final VideoMakerService videoMakerService;

    @Operation(summary = "查询制作人")
    @GetMapping("/video-maker/{id}")
    public Result<VideoMaker> one(@PathVariable Long id) {
        Optional<VideoMaker> item = videoMakerService.findItemById(id);
        return item.map(Result::ok).orElseGet(Result::notFound);
    }

    @Operation(summary = "新增制作人")
    @PostMapping("/video-maker")
    public Result<VideoMaker> insertOne(@RequestBody VideoMakerReqDto dto) {
        Optional<VideoMaker> item = videoMakerService.saveAndReturn(dto);
        return item.map(Result::ok).orElseGet(Result::badRequest);
    }

    @Operation(summary = "更新制作人")
    @PutMapping("/video-maker")
    public Result<VideoMaker> updateOne(@RequestBody VideoMakerReqDto dto) {
        Optional<VideoMaker> item = videoMakerService.updateAndReturn(dto);
        return item.map(Result::ok).orElseGet(Result::badRequest);
    }

    @Operation(summary = "删除制作人")
    @DeleteMapping("/video-maker/{id}")
    public Result<VideoMaker> deleteOne(@PathVariable Long id) {
        Optional<VideoMaker> item = videoMakerService.deleteAndReturnById(id);
        return item.map(Result::ok).orElseGet(Result::badRequest);
    }
}
