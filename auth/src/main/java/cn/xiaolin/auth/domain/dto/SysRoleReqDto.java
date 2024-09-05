package cn.xiaolin.auth.domain.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author xingxiaolin xing.xiaolin@foxmail.com
 * @Description 定义系统角色请求的数据传输对象
 * @create 2023/8/12
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
public class SysRoleReqDto {
    private Long id;

    /**
     * 角色名
     */
    @TableField(value = "name")
    private String name;
}
