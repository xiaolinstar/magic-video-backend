package cn.xiaolin.auth.service;

import cn.xiaolin.auth.domain.dto.SysRolePermReqDto;
import cn.xiaolin.auth.domain.entity.SysRolePermission;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Optional;

/**
 * @author xingxiaolin xing.xiaolin@foxmail.com
 * @Description 系统角色权限服务接口
 * @create 2023/08/10
 */
public interface SysRolePermissionService extends IService<SysRolePermission> {

    Optional<SysRolePermission> findItemById(Long id);

    Optional<SysRolePermission> deleteAndReturnById(Long id);

    Optional<SysRolePermission> updateAndReturn(SysRolePermReqDto dto);

    Optional<SysRolePermission> saveAndReturn(SysRolePermReqDto dto);

}
