package cn.xiaolin.auth.service;

import cn.xiaolin.auth.domain.dto.SysUserRoleReqDto;
import cn.xiaolin.auth.domain.entity.SysUserRole;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Optional;

/**
* @author xlxing
* @description 针对表【sys_user_role(系统用户角色关联表)】的数据库操作Service
* @createDate 2023-08-10 22:24:12
*/
public interface SysUserRoleService extends IService<SysUserRole> {
    Optional<SysUserRole> findItemById(Long id);


    Optional<SysUserRole> deleteAndReturnById(Long id);

    Optional<SysUserRole> updateAndReturn(SysUserRoleReqDto dto);

    Optional<SysUserRole> saveAndReturn(SysUserRoleReqDto dto);
}
