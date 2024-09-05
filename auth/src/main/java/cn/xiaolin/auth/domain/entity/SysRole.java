package cn.xiaolin.auth.domain.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 系统角色
 * @author xingxiaolin xing.xiaolin@foxmail.com
 */
@TableName(value ="sys_role")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SysRole implements Serializable {
    /**
     * 主键
     */
    @TableId(value = "id")
    private Long id;

    /**
     * 角色名
     */
    @TableField(value = "name")
    private String name;

    /**
     * 乐观锁
     */
    @TableField(value = "revision")
    @Version
    @JsonIgnore
    private Integer revision;

    /**
     * 逻辑删除
     */
    @TableField(value = "deleted")
    @TableLogic
    @JsonIgnore
    private Boolean deleted;

    /**
     * 创建时间
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    @JsonIgnore
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    @JsonIgnore
    private LocalDateTime updateTime;

    /**
     * 创建人
     */
    @TableField(value = "created_by_user_id", fill = FieldFill.INSERT)
    @JsonIgnore
    private Long createdByUserId;

    /**
     * 更新人
     */
    @TableField(value = "updated_by_user_id", fill = FieldFill.INSERT_UPDATE)
    @JsonIgnore
    private Long updatedByUserId;

    @Serial
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}