package cn.xiaolin.auth.dubbo;

import cn.xiaolin.auth.domain.entity.SysPermission;
import cn.xiaolin.auth.domain.entity.SysRole;
import cn.xiaolin.auth.service.SysPermissionService;
import cn.xiaolin.auth.service.SysRoleService;
import lombok.RequiredArgsConstructor;
import org.apache.dubbo.config.annotation.DubboService;

import java.util.List;
import java.util.Set;

/**
 * 用户权限Dubbo RPC服务提供者
 * @author xingxiaolin xing.xiaolin@foxmail.com
 * @Description 该类实现了Dubbo框架中的一个服务接口，提供了根据用户ID获取权限和角色的功能。
 * @create 2023/9/9
 */
@DubboService
@RequiredArgsConstructor
public class DubboUserPermService implements cn.xiaolin.api.dubbo.service.UserPermService {
    private final SysPermissionService sysPermissionService;
    private final SysRoleService sysRoleService;

    /**
     * 根据用户ID获取用户的权限列表
     *
     * @param userId 用户ID
     * @return 权限名称列表
     */
    @Override
    public List<String> getPermByUserId(Long userId) {
        List<SysPermission> permissions = sysPermissionService.listPermsByUserId(userId);
        return permissions.stream()
                .map(SysPermission::getName)
                .toList();
    }

    /**
     * 根据用户ID获取用户的角色列表
     *
     * @param userId 用户ID
     * @return 角色名称列表
     */
    @Override
    public List<String> getRoleByUserId(Long userId) {
        List<SysRole> roles = sysRoleService.listRolesByUserId(userId);
        return roles.stream()
                .map(SysRole::getName)
                .toList();
    }

    /**
     * 根据用户ID获取用户的权限列表，包括角色关联的权限
     *
     * @param userId 用户ID
     * @return 权限名称列表
     */
    public List<String> getPermWithRolesByUserId(Long userId) {
        Set<SysPermission> permissions = sysPermissionService.listPermsWithRoleByUserId(userId);
        return permissions.stream()
                .map(SysPermission::getName)
                .toList();
    }


}
