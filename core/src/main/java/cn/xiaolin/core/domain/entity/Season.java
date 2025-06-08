package cn.xiaolin.core.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author xingxiaolin xing.xiaolin@foxmail.com
 * @Description
 * @create 2025/6/8
 */
@Tag(name = "季", description = "剧集-季实体类")
@TableName(value ="season")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Season implements Serializable {
    /**
     * 主键id
     */
    @TableId
    private Long id;

    private Long collectionId;

    private Integer seasonNumber;

    /**
     * 主标题
     */
    private String title;

    /**
     * 描述
     */
    private String description;

    /**
     * 封面url
     */
    private String coverImage;


    private LocalDateTime releaseDate;


    /**
     * 逻辑删除
     */
    @TableLogic
    @JsonIgnore
    private Integer deleted;

    /**
     * 乐观锁
     */
    @Version
    @JsonIgnore
    private Integer revision;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    @JsonIgnore
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    @JsonIgnore
    private LocalDateTime updateTime;

    /**
     * 创建人
     */
    @TableField(fill = FieldFill.INSERT)
    @JsonIgnore
    private Long createdByUserId;

    /**
     * 更新人
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    @JsonIgnore
    private Long updatedByUserId;

    @Serial
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

}
