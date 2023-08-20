package cn.xiaolin.auth.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author xingxiaolin xlxing@bupt.edu.cn
 * @Description
 * @create 2023/8/13
 */

@NoArgsConstructor
@AllArgsConstructor
@Data
public class LoginUserReqDto {
    private String username;
    private String password;
}
