package cn.xiaolin.auth.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author xingxiaolin xing.xiaolin@foxmail.com
 * @Description 定义用于传输系统用户角色关联信息的数据传输对象
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
