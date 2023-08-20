package cn.xiaolin.auth.domain.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author xingxiaolin xlxing@bupt.edu.cn
 * @Description
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
