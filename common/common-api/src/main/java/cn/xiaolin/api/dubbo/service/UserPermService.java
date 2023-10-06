package cn.xiaolin.api.dubbo.service;

import java.util.List;

/**
 *
 * @author xingxiaolin xlxing@bupt.edu.cn
 * @Description
 * @create 2023/9/9
 */
public interface UserPermService {

    List<String> getPermByUserId(Long userId);

    List<String> getRoleByUserId(Long userId);
}
