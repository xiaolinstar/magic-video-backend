package cn.xiaolin.core.controller;

import cn.xiaolin.core.domain.dto.VideoDistrictReqDto;
import cn.xiaolin.core.domain.entity.VideoDistrict;
import cn.xiaolin.core.service.VideoDistrictService;
import cn.xiaolin.utils.constant.ApiRouterConsts;
import cn.xiaolin.utils.resp.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

/**
 * @author xingxiaolin xing.xiaolin@foxmail.com
 * @Description 视频和国家地区关联关系控制器
 * @create 2023/8/6
 */
@Tag(name = "视频和国家地区的关联关系")
@RestController
@RequiredArgsConstructor
@RequestMapping(ApiRouterConsts.API_CORE_PREFIX)
public class VideoDistrictController {

    private final VideoDistrictService videoDistrictService;

    @Operation(summary = "查询视频和国家地区的关联关系")
    @GetMapping("/video-district/{id}")
    public Result<VideoDistrict> one(@PathVariable Long id) {
        Optional<VideoDistrict> item = videoDistrictService.findItemById(id);
        return item.map(Result::ok).orElseGet(Result::notFound);
    }

    @Operation(summary = "新增视频和国家地区的关联关系")
    @PostMapping("/video-district")
    public Result<VideoDistrict> insertOne(@RequestBody VideoDistrictReqDto dto) {
        Optional<VideoDistrict> item = videoDistrictService.saveAndReturn(dto);
        return item.map(Result::ok).orElseGet(Result::badRequest);
    }

    @Operation(summary = "更新视频和国家地区的关联关系")
    @PutMapping("/video-district")
    public Result<VideoDistrict> updateOne(@RequestBody VideoDistrictReqDto dto) {
        Optional<VideoDistrict> item = videoDistrictService.updateAndReturn(dto);
        return item.map(Result::ok).orElseGet(Result::badRequest);
    }

    @Operation(summary = "删除视频和国家地区的关联关系")
    @DeleteMapping("/video-district/{id}")
    public Result<VideoDistrict> deleteOne(@PathVariable Long id) {
        Optional<VideoDistrict> item = videoDistrictService.deleteAndReturnById(id);
        return item.map(Result::ok).orElseGet(Result::badRequest);
    }
}
