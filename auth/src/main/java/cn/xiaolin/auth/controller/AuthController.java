package cn.xiaolin.auth.controller;

import cn.xiaolin.auth.domain.dto.LoginUserReqDto;
import cn.xiaolin.auth.domain.entity.SysUser;
import cn.xiaolin.auth.service.SysUserService;
import cn.xiaolin.utils.constant.ApiRouterConsts;
import cn.xiaolin.utils.resp.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.NotImplementedException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

/**
 * @author xingxiaolin xing.xiaolin@foxmail.com
 * @Description 认证模块控制层
 * @create 2023/8/13
 */
@Tag(name = "认证")
@RestController
@RequiredArgsConstructor
@RequestMapping(ApiRouterConsts.API_AUTH_PREFIX)
public class AuthController {

    private final SysUserService sysUserService;

    @Operation(summary = "用户注册")
    @PostMapping("/register")
    public Result<SysUser> register(@RequestBody LoginUserReqDto dto) {
        Optional<SysUser> sysUser = sysUserService.register(dto.getUsername(), dto.getPassword());
        return sysUser.map(Result::ok).orElseGet(Result::badRequest);
    }

    @Operation(summary = "用户登陆")
    @PostMapping("/login")
    public Result<Void> login(@RequestBody LoginUserReqDto dto) {
        boolean login = sysUserService.login(dto.getUsername(), dto.getPassword());
        return login ? Result.ok() : Result.error();
    }

    @Operation(summary = "用户登出")
    @PostMapping("/logout")
    public Result<Void> logout() {
        sysUserService.logout();
        return Result.ok();
    }

    @Operation(summary = "踢人下线")
    @PostMapping("/kick-out")
    public Result<Void> kickOut(Long id) {
        throw new NotImplementedException();
    }
}
