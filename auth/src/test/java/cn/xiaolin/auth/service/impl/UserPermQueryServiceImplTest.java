package cn.xiaolin.auth.service.impl;

import cn.xiaolin.auth.domain.entity.SysPermission;
import cn.xiaolin.auth.domain.entity.SysRole;
import cn.xiaolin.auth.service.SysPermissionService;
import cn.xiaolin.auth.service.SysRoleService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserPermQueryServiceImplTest {

    @Mock
    private SysPermissionService sysPermissionService;

    @Mock
    private SysRoleService sysRoleService;

    @InjectMocks
    private UserPermQueryServiceImpl userPermQueryService;

    @Test
    void listPermissionNamesMapsEntityNames() {
        when(sysPermissionService.listPermsByUserId(42L))
                .thenReturn(List.of(
                        SysPermission.builder().name("video:read").build(),
                        SysPermission.builder().name("video:write").build()));

        assertEquals(List.of("video:read", "video:write"), userPermQueryService.listPermissionNames(42L));
        verify(sysPermissionService).listPermsByUserId(42L);
    }

    @Test
    void listRoleNamesMapsEntityNames() {
        when(sysRoleService.listRolesByUserId(42L))
                .thenReturn(List.of(
                        SysRole.builder().name("ADMIN").build(),
                        SysRole.builder().name("USER").build()));

        assertEquals(List.of("ADMIN", "USER"), userPermQueryService.listRoleNames(42L));
        verify(sysRoleService).listRolesByUserId(42L);
    }
}
