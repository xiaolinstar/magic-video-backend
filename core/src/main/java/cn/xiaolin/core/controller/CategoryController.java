package cn.xiaolin.core.controller;

import cn.xiaolin.utils.constant.ApiRouterConsts;
import cn.xiaolin.utils.resp.Result;
import cn.xiaolin.core.domain.dto.CategoryReqDto;
import cn.xiaolin.core.domain.entity.Category;
import cn.xiaolin.core.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * @author xingxiaolin xing.xiaolin@foxmail.com
 * @Description 视频类别控制器
 * @create 2023/7/30
 */
@Tag(name = "视频类别")
@RestController
@RequestMapping(ApiRouterConsts.API_CORE_PREFIX)
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;

    @Operation(summary = "查询类别")
    @GetMapping("/category/{id}")
    public Result<Category> one(@PathVariable Long id) {
        Optional<Category> item = categoryService.findItemById(id);
        return item.map(Result::ok).orElseGet(Result::notFound);
    }

    @Operation(summary = "新增类别")
    @PostMapping("/category")
    public Result<Category> insertOne(@RequestBody CategoryReqDto dto) {
        Optional<Category> item = categoryService.saveAndReturn(dto);
        return item.map(Result::ok).orElseGet(Result::badRequest);
    }

    @Operation(summary = "更新类别")
    @PutMapping("/category")
    public Result<Category> updateOne(@RequestBody CategoryReqDto dto) {
        Optional<Category> item = categoryService.updateAndReturn(dto);
        return item.map(Result::ok).orElseGet(Result::badRequest);
    }

    @Operation(summary = "删除类别")
    @DeleteMapping("/category/{id}")
    public Result<Category> deleteOne(@PathVariable Long id) {
        Optional<Category> item = categoryService.deleteAndReturnById(id);
        return item.map(Result::ok).orElseGet(Result::badRequest);
    }

    @Operation(summary = "查询所有类别")
    @GetMapping("/category/all")
    public Result<List<Category>> listCategory() {
        List<Category> categories = categoryService.list();
        return Result.ok(categories);
    }
}
