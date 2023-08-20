package cn.xiaolin.auth.domain.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author xingxiaolin xlxing@bupt.edu.cn
 * @Description
 * @create 2023/8/12
 */

@NoArgsConstructor
@AllArgsConstructor
@Data
public class SysPermReqDto {
    private Long id;
    private String name;
    private String description;
    private Boolean view;
    private Boolean modify;
}
