package cn.xiaolin.core.controller;

import cn.xiaolin.utils.constant.ApiRouterConsts;
import cn.xiaolin.utils.resp.Result;
import cn.xiaolin.core.domain.dto.DistrictReqDto;
import cn.xiaolin.core.domain.entity.District;
import cn.xiaolin.core.service.DistrictService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

/**
 * @author xingxiaolin xing.xiaolin@foxmail.com
 * @Description 国家地区控制器
 * @create 2023/7/30
 */
@Tag(name = "国家和地区")
@RestController
@RequestMapping(ApiRouterConsts.API_CORE_PREFIX)
@RequiredArgsConstructor
public class DistrictController {

    private final DistrictService districtService;

    @Operation(summary = "查询国家和地区")
    @GetMapping("/district/{id}")
    public Result<District> one(@PathVariable Long id) {
        Optional<District> item = districtService.findItemById(id);
        return item.map(Result::ok).orElseGet(Result::notFound);
    }

    @Operation(summary = "增删国家和地区")
    @PostMapping("/district")
    public Result<District> insertOne(@RequestBody DistrictReqDto dto) {
        Optional<District> item = districtService.saveAndReturn(dto);
        return item.map(Result::ok).orElseGet(Result::badRequest);
    }

    @Operation(summary = "更新国家和地区")
    @PutMapping("/district")
    public Result<District> updateOne(@RequestBody DistrictReqDto dto) {
        Optional<District> item = districtService.updateAndReturn(dto);
        return item.map(Result::ok).orElseGet(Result::badRequest);
    }

    @Operation(summary = "删除国家和地区")
    @DeleteMapping("/district/{id}")
    public Result<District> deleteOne(@PathVariable Long id) {
        Optional<District> item = districtService.deleteAndReturnById(id);
        return item.map(Result::ok).orElseGet(Result::badRequest);
    }
}
