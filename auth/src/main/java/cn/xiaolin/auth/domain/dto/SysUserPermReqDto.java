package cn.xiaolin.auth.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author xingxiaolin xing.xiaolin@foxmail.com
 * @Description 定义系统角色权限请求的数据传输对象
 * @create 2023/8/12
 */

@NoArgsConstructor
@AllArgsConstructor
@Data
public class SysUserPermReqDto {
    private Long id;

    /**
     * 用户Id
     */
    private Long sysUserId;

    /**
     * 权限Id
     */
    private Long sysPermissionId;
}
