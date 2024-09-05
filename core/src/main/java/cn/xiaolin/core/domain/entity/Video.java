package cn.xiaolin.core.domain.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.JsonSerializer;
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
     * 中文名称
     */
    private String name;

    /**
     * 封面
     */
    private String icon;

    /**
     * 评分
     */
    private BigDecimal rating;

    /**
     * 年份
     */
    private Integer year;

    /**
     * 发布日期
     */
    private LocalDate releaseDate;

    /**
     * 别名
     */
    private String alias;

    /**
     * 剧情摘要
     */
    private String description;

    /**
     * 资源id
     */
    private Long resourceId;

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