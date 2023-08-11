package cn.xiaolin.core.controller;

import cn.xiaolin.utils.constant.ApiRouterConsts;
import cn.xiaolin.utils.resp.Result;
import cn.xiaolin.core.domain.dto.AwardReqDto;
import cn.xiaolin.core.domain.entity.Award;
import cn.xiaolin.core.service.AwardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

/**
 * @author xingxiaolin xlxing@bupt.edu.cn
 * @Description 奖项控制器
 * @create 2023/7/30
 */

@Tag(name = "奖项", description = "影视剧奖项增删改查")
@RestController
@RequestMapping(ApiRouterConsts.API_CORE_PREFIX)
@RequiredArgsConstructor
public class AwardController {
    private final AwardService awardService;

    @Operation(summary = "查询奖项信息", description = "根据奖项id查询奖项的详细信息")
    @GetMapping("/award/{id}")
    public Result<Award> one(@PathVariable Long id) {
        Optional<Award> item = awardService.findItemById(id);
        return item.map(Result::ok).orElseGet(Result::notFound);
    }

    @Operation(summary = "增加奖项")
    @PostMapping("/award")
    public Result<Award> insertOne(@RequestBody AwardReqDto dto) {
        Optional<Award> item = awardService.saveAndReturn(dto);
        return item.map(Result::ok).orElseGet(Result::badRequest);
    }

    @Operation(summary = "更新奖项")
    @PutMapping("/award")
    public Result<Award> updateOne(@RequestBody AwardReqDto dto) {
        Optional<Award> item = awardService.updateAndReturn(dto);
        return item.map(Result::ok).orElseGet(Result::badRequest);
    }

    @Operation(summary = "删除奖项")
    @DeleteMapping("/award/{id}")
    public Result<Award> deleteOne(@PathVariable Long id) {
        Optional<Award> item = awardService.deleteAndReturnById(id);
        return item.map(Result::ok).orElseGet(Result::badRequest);
    }

}
