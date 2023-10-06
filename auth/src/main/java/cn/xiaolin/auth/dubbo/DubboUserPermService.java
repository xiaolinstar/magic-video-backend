package cn.xiaolin.auth.dubbo;

import cn.xiaolin.auth.domain.entity.SysPermission;
import cn.xiaolin.auth.domain.entity.SysRole;
import cn.xiaolin.auth.service.SysPermissionService;
import cn.xiaolin.auth.service.SysRoleService;
import lombok.RequiredArgsConstructor;
import org.apache.dubbo.config.annotation.DubboService;

import java.util.List;

/**
 * 用户权限Dubbo RPC服务提供者
 * @author xingxiaolin xlxing@bupt.edu.cn
 * @Description
 * @create 2023/9/9
 */
@DubboService
@RequiredArgsConstructor
public class DubboUserPermService implements cn.xiaolin.api.dubbo.service.UserPermService {
    private final SysPermissionService sysPermissionService;
    private final SysRoleService sysRoleService;

    @Override
    public List<String> getPermByUserId(Long userId) {
        List<SysPermission> permissions = sysPermissionService.listPermsByUserId(userId);
        return permissions.stream()
                .map(SysPermission::getName)
                .toList();
    }

    @Override
    public List<String> getRoleByUserId(Long userId) {
        List<SysRole> roles = sysRoleService.listRolesByUserId(userId);
        return roles.stream()
                .map(SysRole::getName)
                .toList();
    }
}
