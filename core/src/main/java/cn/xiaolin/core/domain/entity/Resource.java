package cn.xiaolin.core.domain.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.deser.std.NumberDeserializers;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 视频资源
 * @author xlxing
 * @TableName resource
 */
@TableName(value ="resource")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Resource implements Serializable {
    /**
     * 主键;雪花算法生成，Jackson序列化时转string
     */
    @TableId
    private Long id;

    /**
     * 资源名称
     */
    private String name;

    /**
     * 摘要算法md5值;判断数据库中是否已经存在，避免重复上传
     */
    private String md5;

    /**
     * MP4资源地址
     */
    private String mp4;

    /**
     * HLS资源
     */
    private String m3u8;

    /**
     * DASH资源
     */
    private String mpd;

    /**
     * 封面图
     */
    private String avatar;

    /**
     * 标题
     */
    private String title;
    /**
     * 介绍
     */
    private String description;


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