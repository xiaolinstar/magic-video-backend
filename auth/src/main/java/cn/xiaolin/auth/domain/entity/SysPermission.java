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
 * 系统权限
 * @author xlxing
 * @TableName sys_permission
 */
@TableName(value ="sys_permission")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SysPermission implements Serializable {
    /**
     * 主键;雪花算法生成，Jackson序列化时转string
     */
    @TableId(value = "id")
    private Long id;

    /**
     * 权限名
     */
    @TableField(value = "name")
    private String name;

   @TableField(value = "description")
   private String description;

   @TableField(value = "view")
   private Boolean view;

   @TableField(value = "modify")
   private Boolean modify;

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
    private Boolean deleted;

    /**
     * 创建时间
     */
    @TableField(value = "create_time", fill =  FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /**
     * 创建人
     */
    @TableField(value = "created_by_user_id", fill =  FieldFill.INSERT)
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