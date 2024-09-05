package cn.xiaolin.auth.controller;

import cn.xiaolin.auth.domain.dto.SysUserPermReqDto;
import cn.xiaolin.auth.domain.entity.SysUserPermission;
import cn.xiaolin.auth.service.SysUserPermissionService;
import cn.xiaolin.utils.constant.ApiRouterConsts;
import cn.xiaolin.utils.resp.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

/**
 * @author xingxiaolin xing.xiaolin@foxmail.com
 * @Description 用户权限关联关系控制层
 * @create 2023/8/12
 */
@Tag(name = "用户权限关联关系表")
@RestController
@RequiredArgsConstructor
@RequestMapping(ApiRouterConsts.API_AUTH_PREFIX)
public class SysUserPermController {
    private final SysUserPermissionService sysUserPermissionService;

    @Operation(summary = "查询用户权限关联关系")
    @GetMapping("/user-perm/{id}")
    public Result<SysUserPermission> one(@PathVariable Long id) {
        Optional<SysUserPermission> item = sysUserPermissionService.findItemById(id);
        return item.map(Result::ok).orElseGet(Result::notFound);
    }


    @Operation(summary = "新增用户权限关联关系")
    @PostMapping("/user-perm")
    public Result<SysUserPermission> insertOne(@RequestBody SysUserPermReqDto dto) {
        Optional<SysUserPermission> item = sysUserPermissionService.saveAndReturn(dto);
        return item.map(Result::ok).orElseGet(Result::badRequest);
    }

    @Operation(summary = "更新用户权限关联关系")
    @PutMapping("/user-perm")
    public Result<SysUserPermission> updateOne(@RequestBody SysUserPermReqDto dto) {
        Optional<SysUserPermission> item = sysUserPermissionService.updateAndReturn(dto);
        return item.map(Result::ok).orElseGet(Result::badRequest);
    }

    @Operation(summary = "删除用户权限关联关系")
    @DeleteMapping("/user-perm/{id}")
    public Result<SysUserPermission> deleteOne(@PathVariable Long id) {
        Optional<SysUserPermission> item = sysUserPermissionService.deleteAndReturnById(id);
        return item.map(Result::ok).orElseGet(Result::badRequest);
    }
}
