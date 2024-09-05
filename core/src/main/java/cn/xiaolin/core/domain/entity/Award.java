package cn.xiaolin.core.domain.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 奖项
 * @author xingxiaolin xing.xiaolin@foxmail.com
 */
@TableName(value ="award")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Award implements Serializable {
    /**
     * 主键
     */
    @TableId(value = "id")
    private Long id;

    /**
     * 奖项名称
     */
    private String name;

    /**
     * 封面图
     */
    private String icon;

    /**
     * 年度
     */
    private Integer year;

    /**
     * 国家或地区
     */
    private String country;

    /**
     * 最佳导演
     */
    private Long bestDirectorId;

    /**
     * 最佳导演提名
     */
    private Long bestDirectorNominationId;

    /**
     * 最佳男主角
     */
    private Long bestMaleActorId;

    /**
     * 最佳女主角
     */
    private Long bestFemaleActorId;

    /**
     * 最佳男主角提名
     */
    private Long bestMaleActorNominationId;

    /**
     * 最佳女主角提名
     */
    private Long bestFemaleActorNominationId;

    /**
     * 最佳编剧
     */
    private Long bestScripterId;

    /**
     * 最佳编剧提名
     */
    private Long bestScripterNominationId;

    /**
     * 最佳新人
     */
    private Long bestNewActorId;

    /**
     * 逻辑删除
     */
    @TableLogic
    private Boolean deleted;

    /**
     * 乐观锁
     */
    @Version
    private Integer revision;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /**
     * 创建人
     */
    @TableField(fill = FieldFill.INSERT)
    private Long createdByUserId;

    /**
     * 更新人
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Long updatedByUserId;

    @Serial
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

}