package cn.xiaolin.multimedia.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

import java.util.Objects;

/**
 * @author xingxiaolin xing.xiaolin@foxmail.com
 * @Description
 * @create 2024/10/11
 */

@Getter
public enum ImageTypeEnum {
    JPG("jpg", ".jpg", "image/jpg"),
    JPEG("jpeg", ".jpeg", "image/jpeg"),
    PNG("png", ".png", "image/png"),
    GIF("gif", ".gif", "image/gif"),
    BMP("bmp", ".bmp", "image/bmp"),
    TIFF("tiff", ".tiff", "image/tiff"),
    WEBP("webp", ".webp", "image/webp"),
    SVG("svg", ".svg", "image/svg+xml");

    @JsonValue
    private final String code;
    private final String suffix;
    private final String contentType;

    ImageTypeEnum(String code, String suffix, String contentType) {
        this.code = code;
        this.suffix = suffix;
        this.contentType = contentType;
    }

    @JsonCreator
    public static ImageTypeEnum fromCode(String code) {
        for (ImageTypeEnum type : ImageTypeEnum.values()) {
            if (Objects.equals(code, type.code)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown code: " + code);
    }
}
