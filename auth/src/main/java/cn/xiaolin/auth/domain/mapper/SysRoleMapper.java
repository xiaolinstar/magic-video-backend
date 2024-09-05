package cn.xiaolin.auth.domain.mapper;

import cn.xiaolin.auth.domain.entity.SysRole;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author xingxiaolin xing.xiaolin@foxmail.com
 * @create  2023/08/10
 */
@Mapper
public interface SysRoleMapper extends BaseMapper<SysRole> {

    List<SysRole> listRolesByPermId(@Param("perm_id") Long permId);

    List<SysRole> listRolesByUserId(@Param("user_id") Long userId);
}




