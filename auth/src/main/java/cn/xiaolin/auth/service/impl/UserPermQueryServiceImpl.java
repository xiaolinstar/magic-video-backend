package cn.xiaolin.auth.service.impl;

import cn.xiaolin.auth.domain.entity.SysPermission;
import cn.xiaolin.auth.domain.entity.SysRole;
import cn.xiaolin.auth.service.SysPermissionService;
import cn.xiaolin.auth.service.SysRoleService;
import cn.xiaolin.auth.service.UserPermQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserPermQueryServiceImpl implements UserPermQueryService {

    private final SysPermissionService sysPermissionService;
    private final SysRoleService sysRoleService;

    @Override
    public List<String> listPermissionNames(Long userId) {
        return sysPermissionService.listPermsByUserId(userId).stream()
                .map(SysPermission::getName)
                .toList();
    }

    @Override
    public List<String> listRoleNames(Long userId) {
        return sysRoleService.listRolesByUserId(userId).stream()
                .map(SysRole::getName)
                .toList();
    }
}
