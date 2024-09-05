package cn.xiaolin.auth.controller;

import cn.xiaolin.auth.domain.dto.SysUserRoleReqDto;
import cn.xiaolin.auth.domain.entity.SysUserRole;
import cn.xiaolin.auth.service.SysUserRoleService;
import cn.xiaolin.utils.constant.ApiRouterConsts;
import cn.xiaolin.utils.resp.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

/**
 * @author xingxiaolin xing.xiaolin@foxmail.com
 * @Description 系统用户角色关联关系控制层
 * @create 2023/8/12
 */
@Tag(name = "系统用户角色关联关系")
@RestController
@RequiredArgsConstructor
@RequestMapping(ApiRouterConsts.API_AUTH_PREFIX)
public class SysUserRoleController {
    private final SysUserRoleService sysUserRoleService;

    @Operation(summary = "查询用户角色关联关系")
    @GetMapping("/user-role/{id}")
    public Result<SysUserRole> one(@PathVariable Long id) {
        Optional<SysUserRole> item = sysUserRoleService.findItemById(id);
        return item.map(Result::ok).orElseGet(Result::notFound);
    }

    @Operation(summary = "新增用户角色关联关系")
    @PostMapping("/user-role")
    public Result<SysUserRole> insertOne(@RequestBody SysUserRoleReqDto dto) {
        Optional<SysUserRole> item = sysUserRoleService.saveAndReturn(dto);
        return item.map(Result::ok).orElseGet(Result::badRequest);
    }

    @Operation(summary = "更新用户角色关联关系")
    @PutMapping("/user-role")
    public Result<SysUserRole> updateOne(@RequestBody SysUserRoleReqDto dto) {
        Optional<SysUserRole> item = sysUserRoleService.updateAndReturn(dto);
        return item.map(Result::ok).orElseGet(Result::badRequest);
    }

    @Operation(summary = "删除用户角色关联关系")
    @DeleteMapping("/user-role/{id}")
    public Result<SysUserRole> deleteOne(@PathVariable Long id) {
        Optional<SysUserRole> item = sysUserRoleService.deleteAndReturnById(id);
        return item.map(Result::ok).orElseGet(Result::badRequest);
    }
}
