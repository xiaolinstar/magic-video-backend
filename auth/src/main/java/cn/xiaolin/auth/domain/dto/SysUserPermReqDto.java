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
public class SysUserPermReqDto {
    private Long id;

    /**
     * 用户Id
     */
    @TableField(value = "sys_user_id")
    private Long sysUserId;

    /**
     * 权限Id
     */
    @TableField(value = "sys_permission_id")
    private Long sysPermissionId;
}
