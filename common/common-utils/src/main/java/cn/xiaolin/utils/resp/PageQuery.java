package cn.xiaolin.utils.resp;

import cn.xiaolin.utils.constant.PageConsts;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * 通用分页查询参数。除页码与每页条数外，可通过 {@code sortBy} 传入排序键，
 * 由各服务的实现自行解释。
 *
 * <p>新接口建议定义明确的 DTO 子类（如 {@code VideoPageQuery}），
 * 而不是把所有条件都塞进查询参数。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PageQuery implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /** 页码，从 1 开始 */
    private long current;

    /** 每页条数，会被收敛到 {@link PageConsts#MAX_PAGE_SIZE} 以内 */
    private long size;

    /**
     * 可选排序键，由服务端解释（例如 latest、rating、duration）；
     * 为空时使用实现类的默认顺序。
     */
    private String sortBy;

    /** 排序方向：asc / desc，默认降序 */
    private String direction;

    public long getCurrent() {
        return PageResult.normalizeCurrent(current);
    }

    public long getSize() {
        return PageResult.normalizeSize(size);
    }
}
