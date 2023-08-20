package cn.xiaolin.auth.controller;

import cn.xiaolin.auth.domain.dto.SysRoleReqDto;
import cn.xiaolin.auth.domain.entity.SysRole;
import cn.xiaolin.auth.service.SysRoleService;
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
@Tag(name = "系统角色", description = "权限角色增删改查")
@RestController
@RequiredArgsConstructor
@RequestMapping(ApiRouterConsts.API_AUTH_PREFIX)
public class SysRoleController {

    private final SysRoleService sysRoleService;

    @Operation(summary = "查询系统角色")
    @GetMapping("/role/{id}")
    public Result<SysRole> one(@PathVariable Long id) {
        Optional<SysRole> item = sysRoleService.findItemById(id);
        return item.map(Result::ok).orElseGet(Result::notFound);
    }

    @Operation(summary = "新增系统角色")
    @PostMapping("/role")
    public Result<SysRole> insertOne(@RequestBody SysRoleReqDto dto) {
        Optional<SysRole> item = sysRoleService.saveAndReturn(dto);
        return item.map(Result::ok).orElseGet(Result::badRequest);
    }

    @Operation(summary = "更新系统角色")
    @PutMapping("/role")
    public Result<SysRole> updateOne(@RequestBody SysRoleReqDto dto) {
        Optional<SysRole> item = sysRoleService.updateAndReturn(dto);
        return item.map(Result::ok).orElseGet(Result::badRequest);
    }

    @Operation(summary = "删除系统角色")
    @DeleteMapping("/role/{id}")
    public Result<SysRole> deleteOne(@PathVariable Long id) {
        Optional<SysRole> item = sysRoleService.deleteAndReturnById(id);
        return item.map(Result::ok).orElseGet(Result::badRequest);
    }
}
