package cn.xiaolin.auth.domain.mapper;

import cn.xiaolin.auth.domain.entity.SysUser;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

/**
* @author xlxing
* @description 针对表[sys_user(系统用户)] 的数据库操作Mapper
* @createDate 2023-08-10 22:24:12
* @Entity generator.domain.SysUser
*/
@Mapper
public interface SysUserMapper extends BaseMapper<SysUser> {

    Optional<SysUser> getUserByUsername(@Param("username") String username);
    List<SysUser> listUsersByRoleId(@Param("role_id") Long roleId);
    List<SysUser> listUsersByPermId(@Param("perm_id") Long permId);
    List<SysUser> listUsersWithRoleByPermId(@Param("perm_id") Long permId);
}




