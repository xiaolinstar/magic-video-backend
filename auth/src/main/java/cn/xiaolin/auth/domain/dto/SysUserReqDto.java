package cn.xiaolin.auth.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author xingxiaolin xing.xiaolin@foxmail.com
 * @Description 定义系统用户请求的数据传输对象
 * @create 2023/8/12
 */

@NoArgsConstructor
@AllArgsConstructor
@Data
public class SysUserReqDto {
    private Long id;
    private String username;
    private String email;
    private String phone;
    private String password;
    private Boolean admission;
}
