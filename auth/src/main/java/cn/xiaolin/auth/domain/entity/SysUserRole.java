package cn.xiaolin.auth.domain.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 系统用户角色关联表
 * @author xingxiaolin xing.xiaolin@foxmail.com
 */
@TableName(value ="sys_user_role")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SysUserRole implements Serializable {
    /**
     * 主键
     */
    @TableId(value = "id")
    private Long id;

    /**
     * 用户Id
     */
    @TableField(value = "sys_user_id")
    private Long sysUserId;

    /**
     * 角色Id
     */
    @TableField(value = "sys_role_id")
    private Long sysRoleId;

    /**
     * 乐观锁
     */
    @TableField(value = "revision")
    @Version
    private Integer revision;

    /**
     * 逻辑删除
     */
    @TableField(value = "deleted")
    @TableLogic
    private Integer deleted;

    /**
     * 创建时间
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /**
     * 创建人
     */
    @TableField(value = "created_by_user_id", fill = FieldFill.INSERT)
    private Long createdByUserId;

    /**
     * 更新人
     */
    @TableField(value = "updated_by_user_id", fill = FieldFill.INSERT_UPDATE)
    private Long updatedByUserId;

    @Serial
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}