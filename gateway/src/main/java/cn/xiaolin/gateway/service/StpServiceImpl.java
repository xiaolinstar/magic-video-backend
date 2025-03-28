package cn.xiaolin.gateway.service;

import cn.dev33.satoken.stp.StpInterface;
import cn.xiaolin.gateway.dubbo.consumer.DubboUserPermClient;
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
    private final DubboUserPermClient dubboUserPermClient;

    @Override
    public List<String> getPermissionList(Object id, String loginType) {
        return dubboUserPermClient.getPermissions(Long.valueOf((String) id));
    }

    @Override
    public List<String> getRoleList(Object id, String loginType) {
        return dubboUserPermClient.getRoles(Long.valueOf((String) id));
    }
}
