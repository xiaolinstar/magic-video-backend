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
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

/**
 * @author xingxiaolin xlxing@bupt.edu.cn
 * @Description
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

    @Operation(summary = "新增系统用户")
    @PostMapping("/user")
    public Result<SysUser> insertOne(@RequestBody SysUserReqDto dto) {
        Optional<SysUser> item = sysUserService.saveAndReturn(dto);
        return item.map(Result::ok).orElseGet(Result::badRequest);
    }

    @Operation(summary = "更新系统用户")
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
}
