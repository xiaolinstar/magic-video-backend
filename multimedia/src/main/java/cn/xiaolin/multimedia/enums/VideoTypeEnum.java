package cn.xiaolin.multimedia.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

import java.util.Objects;

/**
 * @author xingxiaolin xing.xiaolin@foxmail.com
 * @Description 文件类型
 * @create 2023/7/29
 */

@Getter
public enum VideoTypeEnum {
    MKV("mkv", ".mkv", "video/mkv"),
    MP4("mp4", ".mp4", "video/mp4");

    @JsonValue
    private final String code;
    private final String suffix;
    private final String contentType;

    VideoTypeEnum(String code, String suffix, String contentType) {
        this.code = code;
        this.suffix = suffix;
        this.contentType = contentType;
    }


    /**
     * 反序列化时根据字符串值找到对应的枚举实例
     * @param code 编码
     * @return 对应的枚举实例
     */
    @JsonCreator
    public static VideoTypeEnum fromCode(String code) {
        for (VideoTypeEnum type : VideoTypeEnum.values()) {
            if (Objects.equals(type.getCode(), code)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown enum code: " + code);
    }
}
