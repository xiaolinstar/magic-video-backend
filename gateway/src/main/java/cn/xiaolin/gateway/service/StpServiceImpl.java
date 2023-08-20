package cn.xiaolin.gateway.service;

import cn.dev33.satoken.stp.StpInterface;
import cn.xiaolin.gateway.grpc.UserPermClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author xingxiaolin xlxing@bupt.edu.cn
 * @Description 自定义鉴权验证接口扩展
 * @create 2023/8/12
 */
@Service
@RequiredArgsConstructor
public class StpServiceImpl implements StpInterface {
    private final UserPermClient userPermClient;

    @Override
    public List<String> getPermissionList(Object id, String loginType) {
        return userPermClient.getPermissions(Long.valueOf((String) id));

    }

    @Override
    public List<String> getRoleList(Object id, String loginType) {
        return userPermClient.getRoles(Long.valueOf((String) id));
    }
}
