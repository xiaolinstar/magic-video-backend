package cn.xiaolin.auth.controller;

import cn.xiaolin.auth.domain.dto.SysPermReqDto;
import cn.xiaolin.auth.domain.entity.SysPermission;
import cn.xiaolin.auth.service.SysPermissionService;
import cn.xiaolin.utils.constant.ApiRouterConsts;
import cn.xiaolin.utils.resp.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

/**
 * @author xingxiaolin xing.xiaolin@foxmail.cok
 * @Description 权限控制层
 * @create 2023/8/12
 */
@Tag(name = "权限", description = "系统权限增删改查")
@RestController
@RequiredArgsConstructor
@RequestMapping(ApiRouterConsts.API_AUTH_PREFIX)
public class SysPermController {
    private final SysPermissionService sysPermissionService;

    @Operation(summary = "查询权限")
    @GetMapping("/perm/{id}")
    public Result<SysPermission> one(@PathVariable Long id) {
        Optional<SysPermission> item = sysPermissionService.findItemById(id);
        return item.map(Result::ok).orElseGet(Result::notFound);
    }

    @Operation(summary = "增加权限")
    @PostMapping("/perm")
    public Result<SysPermission> insertOne(@RequestBody SysPermReqDto dto) {
        Optional<SysPermission> item = sysPermissionService.saveAndReturn(dto);
        return item.map(Result::ok).orElseGet(Result::badRequest);
    }

    @Operation(summary = "更新权限")
    @PutMapping("/perm")
    public Result<SysPermission> updateOne(@RequestBody SysPermReqDto dto) {
        Optional<SysPermission> item = sysPermissionService.updateAndReturn(dto);
        return item.map(Result::ok).orElseGet(Result::badRequest);
    }

    @Operation(summary = "删除权限")
    @DeleteMapping("/perm/{id}")
    public Result<SysPermission> deleteOne(@PathVariable Long id) {
        Optional<SysPermission> item = sysPermissionService.deleteAndReturnById(id);
        return item.map(Result::ok).orElseGet(Result::badRequest);
    }
}
