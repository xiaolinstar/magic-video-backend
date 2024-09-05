package cn.xiaolin.utils.utility;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

/**
 * @author xingxiaolin xing.xiaolin@foxmail.com
 * @Description Jackson工具类
 * @create 2023/7/23
 */
@Slf4j
public class JacksonUtils {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private JacksonUtils() {}
    public static <T> String toJson(T obj) throws JsonProcessingException {
        return OBJECT_MAPPER.writeValueAsString(obj);
    }

    public static <T> T fromJson(String json, Class<T> toClass) throws JsonProcessingException {
        return OBJECT_MAPPER.readValue(json, toClass);
    }

}
