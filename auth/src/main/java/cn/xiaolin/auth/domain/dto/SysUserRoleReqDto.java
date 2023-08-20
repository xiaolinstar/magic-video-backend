package cn.xiaolin.auth.domain.dto;

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
public class SysUserRoleReqDto {
    private Long id;
    private Long sysUserId;
    private Long sysRoleId;
}
