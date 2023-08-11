package cn.xiaolin.utils.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

/**
 * @author xingxiaolin xlxing@bupt.edu.cn
 * @Description
 * @create 2023/8/6
 */
@Getter
public enum Gender {
    MALE("male", "男性"),
    FEMALE("female", "女性");

    @JsonValue
    private final String code;
    private final String name;

    Gender(String code, String name) {
        this.code = code;
        this.name = name;
    }
    @JsonCreator
    public static Gender getGenderByCode(String code) {
        for (Gender gender : Gender.values()) {
            if (StringUtils.equalsAnyIgnoreCase(code, gender.code)) {
                return gender;
            }
        }
        throw new IllegalArgumentException("参数错误，不支持的参数：" + code);
    }
}
