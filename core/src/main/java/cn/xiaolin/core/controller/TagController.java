package cn.xiaolin.core.controller;

import cn.xiaolin.utils.constant.ApiRouterConsts;
import cn.xiaolin.utils.resp.Result;
import cn.xiaolin.core.domain.dto.TagReqDto;
import cn.xiaolin.core.domain.entity.Tag;
import cn.xiaolin.core.service.TagService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

/**
 * @author xingxiaolin xing.xiaolin@foxmail.com
 * @Description 标签控制类
 * @create 2023/7/30
 */

@io.swagger.v3.oas.annotations.tags.Tag(name = "标签")
@RestController
@RequestMapping(ApiRouterConsts.API_CORE_PREFIX)
@RequiredArgsConstructor
public class TagController {
    private final TagService tagService;

    @Operation(summary = "查询标签")
    @GetMapping("/tag/{id}")
    public Result<Tag> one(@PathVariable Long id) {
        Optional<Tag> item = tagService.findItemById(id);
        return item.map(Result::ok).orElseGet(Result::notFound);
    }

    @Operation(summary = "新增标签")
    @PostMapping("/tag")
    public Result<Tag> insertOne(@RequestBody TagReqDto dto) {
        Optional<Tag> item = tagService.saveAndReturn(dto);
        return item.map(Result::ok).orElseGet(Result::badRequest);
    }

    @Operation(summary = "更新标签")
    @PutMapping("/tag")
    public Result<Tag> updateOne(@RequestBody TagReqDto dto) {
        Optional<Tag> item = tagService.updateAndReturn(dto);
        return item.map(Result::ok).orElseGet(Result::badRequest);
    }

    @Operation(summary = "删除标签")
    @DeleteMapping("/tag/{id}")
    public Result<Tag> deleteOne(@PathVariable Long id) {
        Optional<Tag> item = tagService.deleteAndReturnById(id);
        return item.map(Result::ok).orElseGet(Result::badRequest);
    }
}
