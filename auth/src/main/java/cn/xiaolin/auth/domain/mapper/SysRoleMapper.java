package cn.xiaolin.auth.domain.mapper;

import cn.xiaolin.auth.domain.entity.SysRole;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
* @author xlxing
* @description 针对表[sys_role(系统角色)] 的数据库操作Mapper
* @createDate 2023-08-10 22:24:12
* @Entity generator.domain.SysRole
*/
@Mapper
public interface SysRoleMapper extends BaseMapper<SysRole> {

    List<SysRole> listRolesByPermId(@Param("perm_id") Long permId);

    List<SysRole> listRolesByUserId(@Param("user_id") Long userId);
}




