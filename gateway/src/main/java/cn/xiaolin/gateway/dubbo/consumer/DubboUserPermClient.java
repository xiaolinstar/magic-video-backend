package cn.xiaolin.gateway.dubbo.consumer;

import cn.xiaolin.api.dubbo.service.UserPermService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author xingxiaolin xing.xiaolin@foxmail.com
 * @Description Dubbo获取用户权限客户端
 * @create 2023/9/9
 */
@Service
public class DubboUserPermClient {

    @DubboReference
    private UserPermService userPermService;

    public List<String> getPermissions(Long userId) {
        return userPermService.getPermByUserId(userId);
    }

    public List<String> getRoles(Long userId) {
        return userPermService.getRoleByUserId(userId);
    }

}
