package cn.xiaolin.utils.resp;

import cn.xiaolin.utils.constant.PageConsts;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;

/**
 * 通用分页返回结构，与具体 ORM 无关，因此放在 {@code common-utils} 中，
 * 不引入 MyBatis-Plus 依赖。
 *
 * @param <T> 元素类型
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PageResult<T> implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /** 当前页码，从 1 开始（与前端约定一致） */
    private long current;

    /** 每页条数 */
    private long size;

    /** 总记录数（跨所有页） */
    private long total;

    /** 总页数 */
    private long pages;

    /** 当前页数据 */
    private List<T> records;

    /** 构造空结果（页码超出末尾或查询无结果时使用） */
    public static <T> PageResult<T> empty(long current, long size) {
        return PageResult.<T>builder()
                .current(current)
                .size(size)
                .total(0)
                .pages(0)
                .records(Collections.emptyList())
                .build();
    }

    /**
     * 根据 ORM 原始输出构造结果，并计算 {@code pages}
     */
    public static <T> PageResult<T> of(long current, long size, long total, List<T> records) {
        long safeSize = normalizeSize(size);
        long safeCurrent = normalizeCurrent(current);
        long pages = total <= 0 ? 0 : (total + safeSize - 1) / safeSize;
        return PageResult.<T>builder()
                .current(safeCurrent)
                .size(safeSize)
                .total(total)
                .pages(pages)
                .records(records == null ? Collections.emptyList() : records)
                .build();
    }

    /** 把 {@code size} 收敛到各服务共用的安全范围 */
    public static long normalizeSize(long size) {
        if (size <= 0) return PageConsts.DEFAULT_PAGE_SIZE;
        return Math.min(size, PageConsts.MAX_PAGE_SIZE);
    }

    /** 把 {@code current} 收敛为不小于 1 的值 */
    public static long normalizeCurrent(long current) {
        return Math.max(current, 1L);
    }
}
