package cn.xiaolin.utils.utility;

import org.apache.commons.lang3.NotImplementedException;

import java.util.Optional;

/**
 * @author xingxiaolin xing.xiaolin@foxmail.com
 * @Description 上下文工具类
 * @create 2023/7/30
 */
public class ContextUtil {
    private ContextUtil() {
        throw new NotImplementedException();
    }
    private static final ThreadLocal<Long> THREAD_LOCAL = new ThreadLocal<>();

    public static void setUserId(Long id) {
        THREAD_LOCAL.set(id);
    }

    public static Long getUserId() {
        return Optional.ofNullable(THREAD_LOCAL.get()).orElse(0L);
    }
}
