package generator.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 视频
 * @TableName video
 */
@TableName(value ="video")
public class Video {
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
     * 描述
     */
    private String description;

    /**
     * 封面图url
     */
    private String coverImage;

    /**
     * 类型
     */
    private String type;

    /**
     * 发布日期
     */
    private Date releaseDate;

    /**
     * 时长（秒）
     */
    private Integer duration;

    /**
     * 评分
     */
    private BigDecimal rating;

    /**
     * 集合id
     */
    private Long collectionId;

    /**
     * 季id
     */
    private Long seasonId;

    /**
     * 逻辑删除
     */
    private Integer deleted;

    /**
     * 乐观锁
     */
    private Integer revision;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 创建人id
     */
    private Long createdByUserId;

    /**
     * 更新人id
     */
    private Long updatedByUserId;

    /**
     * 主键id
     */
    public Long getId() {
        return id;
    }

    /**
     * 主键id
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * 主标题
     */
    public String getTitle() {
        return title;
    }

    /**
     * 主标题
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * 原标题
     */
    public String getOriginalTitle() {
        return originalTitle;
    }

    /**
     * 原标题
     */
    public void setOriginalTitle(String originalTitle) {
        this.originalTitle = originalTitle;
    }

    /**
     * 描述
     */
    public String getDescription() {
        return description;
    }

    /**
     * 描述
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * 封面图url
     */
    public String getCoverImage() {
        return coverImage;
    }

    /**
     * 封面图url
     */
    public void setCoverImage(String coverImage) {
        this.coverImage = coverImage;
    }

    /**
     * 类型
     */
    public String getType() {
        return type;
    }

    /**
     * 类型
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * 发布日期
     */
    public Date getReleaseDate() {
        return releaseDate;
    }

    /**
     * 发布日期
     */
    public void setReleaseDate(Date releaseDate) {
        this.releaseDate = releaseDate;
    }

    /**
     * 时长（秒）
     */
    public Integer getDuration() {
        return duration;
    }

    /**
     * 时长（秒）
     */
    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    /**
     * 评分
     */
    public BigDecimal getRating() {
        return rating;
    }

    /**
     * 评分
     */
    public void setRating(BigDecimal rating) {
        this.rating = rating;
    }

    /**
     * 集合id
     */
    public Long getCollectionId() {
        return collectionId;
    }

    /**
     * 集合id
     */
    public void setCollectionId(Long collectionId) {
        this.collectionId = collectionId;
    }

    /**
     * 季id
     */
    public Long getSeasonId() {
        return seasonId;
    }

    /**
     * 季id
     */
    public void setSeasonId(Long seasonId) {
        this.seasonId = seasonId;
    }

    /**
     * 逻辑删除
     */
    public Integer getDeleted() {
        return deleted;
    }

    /**
     * 逻辑删除
     */
    public void setDeleted(Integer deleted) {
        this.deleted = deleted;
    }

    /**
     * 乐观锁
     */
    public Integer getRevision() {
        return revision;
    }

    /**
     * 乐观锁
     */
    public void setRevision(Integer revision) {
        this.revision = revision;
    }

    /**
     * 创建时间
     */
    public Date getCreateTime() {
        return createTime;
    }

    /**
     * 创建时间
     */
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    /**
     * 更新时间
     */
    public Date getUpdateTime() {
        return updateTime;
    }

    /**
     * 更新时间
     */
    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    /**
     * 创建人id
     */
    public Long getCreatedByUserId() {
        return createdByUserId;
    }

    /**
     * 创建人id
     */
    public void setCreatedByUserId(Long createdByUserId) {
        this.createdByUserId = createdByUserId;
    }

    /**
     * 更新人id
     */
    public Long getUpdatedByUserId() {
        return updatedByUserId;
    }

    /**
     * 更新人id
     */
    public void setUpdatedByUserId(Long updatedByUserId) {
        this.updatedByUserId = updatedByUserId;
    }

    @Override
    public boolean equals(Object that) {
        if (this == that) {
            return true;
        }
        if (that == null) {
            return false;
        }
        if (getClass() != that.getClass()) {
            return false;
        }
        Video other = (Video) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getTitle() == null ? other.getTitle() == null : this.getTitle().equals(other.getTitle()))
            && (this.getOriginalTitle() == null ? other.getOriginalTitle() == null : this.getOriginalTitle().equals(other.getOriginalTitle()))
            && (this.getDescription() == null ? other.getDescription() == null : this.getDescription().equals(other.getDescription()))
            && (this.getCoverImage() == null ? other.getCoverImage() == null : this.getCoverImage().equals(other.getCoverImage()))
            && (this.getType() == null ? other.getType() == null : this.getType().equals(other.getType()))
            && (this.getReleaseDate() == null ? other.getReleaseDate() == null : this.getReleaseDate().equals(other.getReleaseDate()))
            && (this.getDuration() == null ? other.getDuration() == null : this.getDuration().equals(other.getDuration()))
            && (this.getRating() == null ? other.getRating() == null : this.getRating().equals(other.getRating()))
            && (this.getCollectionId() == null ? other.getCollectionId() == null : this.getCollectionId().equals(other.getCollectionId()))
            && (this.getSeasonId() == null ? other.getSeasonId() == null : this.getSeasonId().equals(other.getSeasonId()))
            && (this.getDeleted() == null ? other.getDeleted() == null : this.getDeleted().equals(other.getDeleted()))
            && (this.getRevision() == null ? other.getRevision() == null : this.getRevision().equals(other.getRevision()))
            && (this.getCreateTime() == null ? other.getCreateTime() == null : this.getCreateTime().equals(other.getCreateTime()))
            && (this.getUpdateTime() == null ? other.getUpdateTime() == null : this.getUpdateTime().equals(other.getUpdateTime()))
            && (this.getCreatedByUserId() == null ? other.getCreatedByUserId() == null : this.getCreatedByUserId().equals(other.getCreatedByUserId()))
            && (this.getUpdatedByUserId() == null ? other.getUpdatedByUserId() == null : this.getUpdatedByUserId().equals(other.getUpdatedByUserId()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getTitle() == null) ? 0 : getTitle().hashCode());
        result = prime * result + ((getOriginalTitle() == null) ? 0 : getOriginalTitle().hashCode());
        result = prime * result + ((getDescription() == null) ? 0 : getDescription().hashCode());
        result = prime * result + ((getCoverImage() == null) ? 0 : getCoverImage().hashCode());
        result = prime * result + ((getType() == null) ? 0 : getType().hashCode());
        result = prime * result + ((getReleaseDate() == null) ? 0 : getReleaseDate().hashCode());
        result = prime * result + ((getDuration() == null) ? 0 : getDuration().hashCode());
        result = prime * result + ((getRating() == null) ? 0 : getRating().hashCode());
        result = prime * result + ((getCollectionId() == null) ? 0 : getCollectionId().hashCode());
        result = prime * result + ((getSeasonId() == null) ? 0 : getSeasonId().hashCode());
        result = prime * result + ((getDeleted() == null) ? 0 : getDeleted().hashCode());
        result = prime * result + ((getRevision() == null) ? 0 : getRevision().hashCode());
        result = prime * result + ((getCreateTime() == null) ? 0 : getCreateTime().hashCode());
        result = prime * result + ((getUpdateTime() == null) ? 0 : getUpdateTime().hashCode());
        result = prime * result + ((getCreatedByUserId() == null) ? 0 : getCreatedByUserId().hashCode());
        result = prime * result + ((getUpdatedByUserId() == null) ? 0 : getUpdatedByUserId().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", title=").append(title);
        sb.append(", originalTitle=").append(originalTitle);
        sb.append(", description=").append(description);
        sb.append(", coverImage=").append(coverImage);
        sb.append(", type=").append(type);
        sb.append(", releaseDate=").append(releaseDate);
        sb.append(", duration=").append(duration);
        sb.append(", rating=").append(rating);
        sb.append(", collectionId=").append(collectionId);
        sb.append(", seasonId=").append(seasonId);
        sb.append(", deleted=").append(deleted);
        sb.append(", revision=").append(revision);
        sb.append(", createTime=").append(createTime);
        sb.append(", updateTime=").append(updateTime);
        sb.append(", createdByUserId=").append(createdByUserId);
        sb.append(", updatedByUserId=").append(updatedByUserId);
        sb.append("]");
        return sb.toString();
    }
}