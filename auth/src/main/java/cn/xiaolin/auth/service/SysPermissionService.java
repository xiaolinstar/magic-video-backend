package cn.xiaolin.auth.service;

import cn.xiaolin.auth.domain.dto.SysPermReqDto;
import cn.xiaolin.auth.domain.entity.SysPermission;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
* @author xlxing
* @description 针对表【sys_permission(系统权限)】的数据库操作Service
* @createDate 2023-08-10 22:24:12
*/
public interface SysPermissionService extends IService<SysPermission> {

    /**
     * 根据权限id，查询权限信息
     * @param id 权限id
     * @return 权限
     */
    Optional<SysPermission> findItemById(Long id);


    /**
     * 删除权限
     * @param id 权限id
     * @return 权限
     */
    Optional<SysPermission> deleteAndReturnById(Long id);

    /**
     * 更新权限
     * @param dto 权限更新请求
     * @return 更新后的权限
     */
    Optional<SysPermission> updateAndReturn(SysPermReqDto dto);

    /**
     * 新增权限
     * @param dto 新增权限请求
     * @return 新增权限
     */
    Optional<SysPermission> saveAndReturn(SysPermReqDto dto);

    /**
     * 查询角色具有的所有权限
     * @param roleId 角色id
     * @return 系统权限
     */
    List<SysPermission> listPermByRoleId(Long roleId);

    /**
     * 查询用户userId的权限
     * @param userId 用户id
     * @return 系统权限
     */
    List<SysPermission> listPermsByUserId(Long userId);

    /**
     * 查询用户userId的所有权限，包含角色拥有的权限
     * @param userId 用户id
     * @return 全部权限
     */
    Set<SysPermission> listPermsWithRoleByUserId(Long userId);
}
