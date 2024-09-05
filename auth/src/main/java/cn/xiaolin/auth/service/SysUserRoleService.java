package cn.xiaolin.auth.service;

import cn.xiaolin.auth.domain.dto.SysUserRoleReqDto;
import cn.xiaolin.auth.domain.entity.SysUserRole;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Optional;

/**
 * @author xingxiaolin xing.xiaolin@foxmail.com
 * @Description 系统用户角色服务接口
 * @create 2023/08/10
 */
public interface SysUserRoleService extends IService<SysUserRole> {
    Optional<SysUserRole> findItemById(Long id);


    Optional<SysUserRole> deleteAndReturnById(Long id);

    Optional<SysUserRole> updateAndReturn(SysUserRoleReqDto dto);

    Optional<SysUserRole> saveAndReturn(SysUserRoleReqDto dto);
}
