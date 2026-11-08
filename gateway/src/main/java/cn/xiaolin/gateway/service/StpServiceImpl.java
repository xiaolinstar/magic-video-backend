package cn.xiaolin.gateway.service;

import cn.dev33.satoken.stp.StpInterface;
import cn.xiaolin.gateway.client.AuthPermClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 实现 StpInterface 接口
 * @author xingxiaolin xing.xiaolin@foxmail.com
 * @Description 自定义鉴权验证接口扩展
 * @create 2023/8/12
 */
@Service
@RequiredArgsConstructor
public class StpServiceImpl implements StpInterface {
    private final AuthPermClient authPermClient;

    @Override
    public List<String> getPermissionList(Object id, String loginType) {
        return authPermClient.getPermissions(Long.valueOf((String) id));
    }

    @Override
    public List<String> getRoleList(Object id, String loginType) {
        return authPermClient.getRoles(Long.valueOf((String) id));
    }
}
