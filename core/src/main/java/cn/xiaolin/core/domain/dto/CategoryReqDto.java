package cn.xiaolin.core.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author xingxiaolin xing.xiaolin@foxmail.com
 * @Description 视频类别请求数据传输对象
 * @create 2023/7/30
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
public class CategoryReqDto {

    private Long id;
    private String name;
}
