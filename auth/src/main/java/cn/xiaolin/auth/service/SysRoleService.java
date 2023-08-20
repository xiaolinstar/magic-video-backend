package cn.xiaolin.auth.service;

import cn.xiaolin.auth.domain.dto.SysRoleReqDto;
import cn.xiaolin.auth.domain.entity.SysRole;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Optional;

/**
* @author xlxing
* @description 针对表【sys_role(系统角色)】的数据库操作Service
* @createDate 2023-08-10 22:24:12
*/
public interface SysRoleService extends IService<SysRole> {


    Optional<SysRole> findItemById(Long id);


    Optional<SysRole> deleteAndReturnById(Long id);

    Optional<SysRole> updateAndReturn(SysRoleReqDto dto);

    Optional<SysRole> saveAndReturn(SysRoleReqDto dto);


    /**
     * 查询具有权限id的所有角色
     * @param permId 权限id
     * @return 系统角色列表
     */
    List<SysRole> listRolesByPermId(Long permId);

    /**
     * 查询用户id的所有角色
     * @param userId 用户id
     * @return 系统角色列表
     */
    List<SysRole> listRolesByUserId(Long userId);


}
