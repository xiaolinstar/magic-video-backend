package cn.xiaolin.auth.service;

import cn.xiaolin.auth.domain.dto.SysPermReqDto;
import cn.xiaolin.auth.domain.entity.SysPermission;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * @author xingxiaolin xing.xiaolin@foxmail.com
 * @Description 系统权限服务接口
 * @create 2023/08/10
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
     * 根据username查询用户权限
     * @param username 用户名
     * @return 权限列表
     */
    List<SysPermission> listPermsByUsername(String username);


    /**
     * 查询用户userId的所有权限，包含角色拥有的权限
     * @param userId 用户id
     * @return 全部权限
     */
    Set<SysPermission> listPermsWithRoleByUserId(Long userId);

    /**
     * 根据username查询用户权限，包括通过角色间接拥有的权限
     * @param username 用户名
     * @return 权限列表
     */
    Set<SysPermission> listPermsWithRoleByUsername(String username);
}
