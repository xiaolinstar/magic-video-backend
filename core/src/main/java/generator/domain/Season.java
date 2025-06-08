package generator.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;
import lombok.Data;

/**
 * 季
 * @TableName season
 */
@TableName(value ="season")
@Data
public class Season {
    /**
     * 主键;雪花算法生成，Jackson序列化时转string
     */
    @TableId
    private Long id;

    /**
     * 集合id
     */
    private Long collectionId;

    /**
     * 季号
     */
    private Integer seasonNumber;

    /**
     * 标题
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

    /**
     * 发布日期
     */
    private Date releaseDate;

    /**
     * 乐观锁
     */
    private Integer revision;

    /**
     * 逻辑删除
     */
    private Integer deleted;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 创建人
     */
    private Long createdByUserId;

    /**
     * 更新人
     */
    private Long updatedByUserId;
}