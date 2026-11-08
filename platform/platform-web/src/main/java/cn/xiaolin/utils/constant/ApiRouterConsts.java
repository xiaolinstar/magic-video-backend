package cn.xiaolin.utils.constant;

/**
 * @author xingxiaolin xing.xiaolin@foxmail.com
 * @Description API路由常量
 * @create 2023/7/23
 */
public class ApiRouterConsts {
    private ApiRouterConsts() {
        throw new IllegalStateException("常量类，不支持实例化");
    }

    public static final String API_VERSION = "/api/v1";

    public static final String API_MULTIMEDIA_PREFIX = API_VERSION + "/multimedia";
    public static final String API_CORE_PREFIX = API_VERSION + "/core";
    public static final String API_AUTH_PREFIX = API_VERSION + "/auth";

}
