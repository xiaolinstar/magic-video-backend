package cn.xiaolin.auth.controller;

import cn.xiaolin.auth.domain.dto.SysRolePermReqDto;
import cn.xiaolin.auth.domain.entity.SysRolePermission;
import cn.xiaolin.auth.service.SysRolePermissionService;
import cn.xiaolin.utils.constant.ApiRouterConsts;
import cn.xiaolin.utils.resp.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

/**
 * @author xingxiaolin xing.xiaolin@foxmail.com
 * @Description 角色权限关联关系控制层
 * @create 2023/8/12
 */
@Tag(name = "角色权限关联关系")
@RestController
@RequiredArgsConstructor
@RequestMapping(ApiRouterConsts.API_AUTH_PREFIX)
public class SysRolePermController {
    private final SysRolePermissionService sysRolePermissionService;

    @Operation(summary = "查询角色权限关联关系")
    @GetMapping("/role-perm/{id}")
    public Result<SysRolePermission> one(@PathVariable Long id) {
        Optional<SysRolePermission> item = sysRolePermissionService.findItemById(id);
        return item.map(Result::ok).orElseGet(Result::notFound);
    }

    @Operation(summary = "新增角色权限关联关系")
    @PostMapping("/role-perm")
    public Result<SysRolePermission> insertOne(@RequestBody SysRolePermReqDto dto) {
        Optional<SysRolePermission> item = sysRolePermissionService.saveAndReturn(dto);
        return item.map(Result::ok).orElseGet(Result::badRequest);
    }

    @Operation(summary = "更新角色权限关联关系")
    @PutMapping("/role-perm")
    public Result<SysRolePermission> updateOne(@RequestBody SysRolePermReqDto dto) {
        Optional<SysRolePermission> item = sysRolePermissionService.updateAndReturn(dto);
        return item.map(Result::ok).orElseGet(Result::badRequest);
    }

    @Operation(summary = "删除角色权限关联关系")
    @DeleteMapping("/role-perm/{id}")
    public Result<SysRolePermission> deleteOne(@PathVariable Long id) {
        Optional<SysRolePermission> item = sysRolePermissionService.deleteAndReturnById(id);
        return item.map(Result::ok).orElseGet(Result::badRequest);
    }
}
