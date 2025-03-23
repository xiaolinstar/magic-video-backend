package cn.xiaolin.auth.controller;

import cn.xiaolin.auth.domain.dto.LoginUserReqDto;
import cn.xiaolin.auth.domain.dto.SysUserReqDto;
import cn.xiaolin.auth.domain.entity.SysUser;
import cn.xiaolin.auth.service.SysUserService;
import cn.xiaolin.utils.constant.ApiRouterConsts;
import cn.xiaolin.utils.resp.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.NotImplementedException;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * @author xingxiaolin xing.xiaolin@foxmail.com
 * @Description 系统用户控制层
 * @create 2023/8/12
 */

@Tag(name = "系统用户", description = "系统用户增删改查")
@RestController
@RequiredArgsConstructor
@RequestMapping(ApiRouterConsts.API_AUTH_PREFIX)
public class SysUserController {
    private final SysUserService sysUserService;

    @Operation(summary = "查询系统用户")
    @GetMapping("/user/{id}")
    public Result<SysUser> one(@PathVariable Long id) {
        Optional<SysUser> item = sysUserService.findItemById(id);
        return item.map(Result::ok).orElseGet(Result::notFound);
    }

    @Operation(summary = "新增系统用户个人资料")
    @PostMapping("/user")
    public Result<SysUser> insertOne(@RequestBody SysUserReqDto dto) {
        Optional<SysUser> item = sysUserService.saveAndReturn(dto);
        return item.map(Result::ok).orElseGet(Result::badRequest);
    }

    @Operation(summary = "更新用户个人资料")
    @PutMapping("/user")
    public Result<SysUser> updateOne(@RequestBody SysUserReqDto dto) {
        Optional<SysUser> item = sysUserService.updateAndReturn(dto);
        return item.map(Result::ok).orElseGet(Result::badRequest);
    }

    @Operation(summary = "删除系统用户")
    @DeleteMapping("/user/{id}")
    public Result<SysUser> deleteOne(@PathVariable Long id) {
        Optional<SysUser> item = sysUserService.deleteAndReturnById(id);
        return item.map(Result::ok).orElseGet(Result::badRequest);
    }

    @Operation(summary = "查询所有用户")
    @GetMapping("/user")
    public Result<List<SysUser>> listAll() {
        List<SysUser> sysUserList = sysUserService.list();
        return Result.ok(sysUserList);
    }

    @Operation(summary = "根据用户id，查询所有权限")
    @GetMapping("/user/perm/{id}")
    public Result<List<String>> listPermissionsByUserId(@PathVariable Long id) {
        throw new NotImplementedException();
    }

    @Operation(summary = "根据用户id，查询所有角色")
    @GetMapping("/user/role/{id}")
    public Result<List<String>> listRolesByUserId(@PathVariable Long id) {
        throw new NotImplementedException();
    }

    @Operation(summary = "查询具有权限permId的所有用户，参数withRole表示通过role间接拥有权限")
    @GetMapping("/user/having-perm/{id}")
    public Result<List<SysUser>> listUsersHavingPermByPermId(@PathVariable Long id, @RequestParam(required = false) Boolean withRole) {
        List<SysUser> users;
        if (Objects.isNull(withRole) || Objects.equals(Boolean.FALSE, withRole)) {
            users = sysUserService.listUsersByPermId(id);
        } else {
            users = sysUserService.listUsersWithRoleByPermId(id);
        }
        return Result.ok(users);
    }

    @Operation(summary = "查询具有角色roleId的所有用户")
    @GetMapping("/user/having-role/{id}")
    public Result<List<SysUser>> listUsersHavingRoleByRoleId(@PathVariable Long id) {
        List<SysUser> users = sysUserService.listUsersByRoleId(id);
        return Result.ok(users);
    }

}
