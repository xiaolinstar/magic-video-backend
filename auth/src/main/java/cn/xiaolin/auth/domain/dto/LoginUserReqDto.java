package cn.xiaolin.auth.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author xingxiaolin xing.xiaolin@foxmail.com
 * @Description 用户登录请求的数据传输对象
 * @create 2023/8/13
 */

@NoArgsConstructor
@AllArgsConstructor
@Data
public class LoginUserReqDto {
    private String username;
    private String password;
}
