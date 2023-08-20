package cn.xiaolin.auth.service;

import cn.xiaolin.auth.domain.dto.SysRoleReqDto;
import cn.xiaolin.auth.domain.dto.SysUserPermReqDto;
import cn.xiaolin.auth.domain.entity.SysRole;
import cn.xiaolin.auth.domain.entity.SysUserPermission;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Optional;

/**
* @author xlxing
* @description 针对表【sys_user_permission(系统用户权限关联表)】的数据库操作Service
* @createDate 2023-08-10 22:24:12
*/
public interface SysUserPermissionService extends IService<SysUserPermission> {
    Optional<SysUserPermission> findItemById(Long id);


    Optional<SysUserPermission> deleteAndReturnById(Long id);

    Optional<SysUserPermission> updateAndReturn(SysUserPermReqDto dto);

    Optional<SysUserPermission> saveAndReturn(SysUserPermReqDto dto);
}
