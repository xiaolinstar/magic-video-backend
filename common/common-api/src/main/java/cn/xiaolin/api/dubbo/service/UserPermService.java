package cn.xiaolin.api.dubbo.service;

import java.util.List;

/**
 *
 * @author xingxiaolin xlxing@bupt.edu.cn
 * @Description
 * @create 2023/9/9
 */
public interface UserPermService {

    /**
     * 查询用户所有权限
     */
    List<String> getPermByUserId(Long userId);

    /**
     * 查询用户所有角色
     */
    List<String> getRoleByUserId(Long userId);

    /**
     * 查询用户所有权限，包括通过角色间接拥有的权限
     */
    List<String> getPermWithRolesByUserId(Long userId);
}
