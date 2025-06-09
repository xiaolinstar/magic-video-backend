package cn.xiaolin.core.domain.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 视频
 * @author xingxiaolin xing.xiaolin@foxmail.com
 */
@Tag(name = "视频", description = "视频信息实体类")
@TableName(value ="video")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Video implements Serializable {
    /**
     * 主键id
     */
    @TableId
    private Long id;

    /**
     * 主标题
     */
    private String title;

    /**
     * 原标题
     */
    private String originalTitle;

    /**
     * 剧情摘要
     */
    private String description;

    /**
     * 封面url
     */
    private String coverImage;

    /**
     * 类型 movie episode clip
     */
    private String type;

    private LocalDateTime releaseDate;

    private int duration;

    /**
     * 评分
     */
    private BigDecimal rating;

    private Long parentId;  // collectionId seasonId

    private Integer sortOrder; // 部或集数（排序）

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