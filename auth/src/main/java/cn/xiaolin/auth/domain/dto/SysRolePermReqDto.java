package cn.xiaolin.auth.domain.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author xingxiaolin xing.xiaolin@foxmail.com
 * @Description 定义角色权限关联关系请求的数据传输对象
 * @create 2023/8/12
 */

@NoArgsConstructor
@AllArgsConstructor
@Data
public class SysRolePermReqDto {
    private Long id;

    /**
     * 系统角色Id
     */
    @TableField(value = "sys_role_id")
    private Long sysRoleId;

    /**
     * 系统权限Id
     */
    @TableField(value = "sys_permission_id")
    private Long sysPermissionId;
}
