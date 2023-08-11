package cn.xiaolin.core.domain.entity;

import cn.xiaolin.utils.enums.Gender;
import com.baomidou.mybatisplus.annotation.*;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 视频制作人
 * @author xlxing
 * @TableName video_maker
 */
@TableName(value ="video_maker")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VideoMaker implements Serializable {
    /**
     * 主键
     */
    @TableId
    private Long id;

    /**
     * 名称
     */
    private String name;

    /**
     * 性别
     */
    private Gender gender;

    /**
     * 星座
     */
    private String constellation;

    /**
     * 封面图
     */
    private String icon;

    /**
     * 生日
     */
    private LocalDate birthday;

    /**
     * 介绍
     */
    private String introduction;

    /**
     * 个人照片集目录
     */
    private String photoPath;

    /**
     * 是否是演员
     */
    private Boolean isActor;

    /**
     * 是否是编剧
     */
    private Boolean isScripter;

    /**
     * 是否是导演
     */
    private Boolean isDirector;

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
    @JsonIgnore
    private static final long serialVersionUID = 1L;

}