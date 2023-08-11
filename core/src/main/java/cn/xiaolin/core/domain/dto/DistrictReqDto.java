package cn.xiaolin.core.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author xingxiaolin xlxing@bupt.edu.cn
 * @Description 地区请求
 * @create 2023/7/30
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class DistrictReqDto {
    private Long id;

    /**
     * 名称
     */
    private String name;

    /**
     * 首都
     */
    private String capital;
}
