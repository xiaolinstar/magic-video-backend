package cn.xiaolin.auth.domain.mapper;

import cn.xiaolin.auth.domain.entity.SysPermission;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

/**
 * @author xingxiaolin xing.xiaolin@foxmail.com
 * @create 2023/08/10
 */
@Mapper
public interface SysPermissionMapper extends BaseMapper<SysPermission> {

    List<SysPermission> listPermByRoleId(@Param("role_id") Long roleId);

    List<SysPermission> listPermsByUserId(@Param("user_id") Long userId);
    List<SysPermission> listPermsByUsername(@Param("username") String username);



    Set<SysPermission> listPermsWithRoleByUserId(@Param("user_id") Long userId);

    Set<SysPermission> listPermsWithRoleByUsername(@Param("username") String username);



}




