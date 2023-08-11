package cn.xiaolin.core.domain.dto;

import com.baomidou.mybatisplus.annotation.TableId;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * @author xingxiaolin xlxing@bupt.edu.cn
 * @Description
 * @create 2023/7/30
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class VideoReqDto {
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
}
