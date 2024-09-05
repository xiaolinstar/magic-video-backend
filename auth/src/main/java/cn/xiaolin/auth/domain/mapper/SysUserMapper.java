package cn.xiaolin.auth.domain.mapper;

import cn.xiaolin.auth.domain.entity.SysUser;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

/**
 * @author xingxiaolin xing.xiaolin@foxmail.com
 * @create  2023/08/10
 */
@Mapper
public interface SysUserMapper extends BaseMapper<SysUser> {

    Optional<SysUser> getUserByUsername(@Param("username") String username);
    List<SysUser> listUsersByRoleId(@Param("role_id") Long roleId);
    List<SysUser> listUsersByPermId(@Param("perm_id") Long permId);
    List<SysUser> listUsersWithRoleByPermId(@Param("perm_id") Long permId);
}




