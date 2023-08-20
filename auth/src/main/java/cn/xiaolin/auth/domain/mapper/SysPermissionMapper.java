package cn.xiaolin.auth.domain.mapper;

import cn.xiaolin.auth.domain.entity.SysPermission;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

/**
* @author xlxing
* @description 针对表[sys_permission(系统权限)] 的数据库操作Mapper
* @createDate 2023-08-10 22:24:12
* @Entity generator.domain.SysPermission
*/
@Mapper
public interface SysPermissionMapper extends BaseMapper<SysPermission> {

    List<SysPermission> listPermByRoleId(@Param("role_id") Long roleId);

    List<SysPermission> listPermsByUserId(@Param("user_id") Long userId);

    Set<SysPermission> listPermsWithRoleByUserId(@Param("user_id") Long userId);
}




