package cn.xiaolin.core.domain.dto;

import cn.xiaolin.utils.enums.Gender;
import lombok.*;

import java.time.LocalDate;

/**
 * @author xingxiaolin xing.xiaolin@foxmail.com
 * @Description
 * @create 2023/7/30
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class VideoMakerReqDto {
    /**
     * 主键
     */
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
}
