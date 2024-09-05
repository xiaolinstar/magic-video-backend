package cn.xiaolin.auth.service;

import cn.xiaolin.auth.domain.dto.SysRoleReqDto;
import cn.xiaolin.auth.domain.dto.SysUserPermReqDto;
import cn.xiaolin.auth.domain.entity.SysRole;
import cn.xiaolin.auth.domain.entity.SysUserPermission;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Optional;

/**
 * @author xingxiaolin xing.xiaolin@foxmail.com
 * @Description 系统用户权限服务接口
 * @create 2023/08/10
 */
public interface SysUserPermissionService extends IService<SysUserPermission> {
    Optional<SysUserPermission> findItemById(Long id);


    Optional<SysUserPermission> deleteAndReturnById(Long id);

    Optional<SysUserPermission> updateAndReturn(SysUserPermReqDto dto);

    Optional<SysUserPermission> saveAndReturn(SysUserPermReqDto dto);
}
