package cn.xiaolin.auth.domain.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author xingxiaolin xing.xiaolin@foxmail.com
 * @Description 定义权限请求数据传输对象
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
