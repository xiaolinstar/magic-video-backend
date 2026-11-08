package cn.xiaolin.utils.constant;

/**
 * 各服务共用的分页默认值。
 */
public final class PageConsts {

    /** 调用方未指定时的默认每页条数 */
    public static final long DEFAULT_PAGE_SIZE = 20L;

    /** 单页条数上限，防止误触发全表查询 */
    public static final long MAX_PAGE_SIZE = 200L;

    private PageConsts() {
        throw new UnsupportedOperationException("常量类不允许实例化");
    }
}
