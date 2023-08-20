package cn.xiaolin.auth.service;

import cn.xiaolin.auth.domain.dto.SysRolePermReqDto;
import cn.xiaolin.auth.domain.entity.SysRolePermission;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Optional;

/**
* @author xlxing
* @description 针对表【sys_role_permission(系统角色权限关联表)】的数据库操作Service
* @createDate 2023-08-10 22:24:12
*/
public interface SysRolePermissionService extends IService<SysRolePermission> {

    Optional<SysRolePermission> findItemById(Long id);

    Optional<SysRolePermission> deleteAndReturnById(Long id);

    Optional<SysRolePermission> updateAndReturn(SysRolePermReqDto dto);

    Optional<SysRolePermission> saveAndReturn(SysRolePermReqDto dto);

}
