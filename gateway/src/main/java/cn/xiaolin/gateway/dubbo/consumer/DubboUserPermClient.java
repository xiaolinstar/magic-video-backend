package cn.xiaolin.gateway.dubbo.consumer;

import cn.xiaolin.api.dubbo.service.UserPermService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 通过 Dubbo 调用 auth 微服务的权限查询服务。
 *
 * <p>直连模式：不使用注册中心，地址由 {@code dubbo.auth.host} / {@code dubbo.auth.port} 指定
 * （各 profile 的 bootstrap 文件里按部署环境给出默认值）。
 * 原因见 {@code docs/adr/0008-remove-nacos.md}。
 *
 * @author xingxiaolin xing.xiaolin@foxmail.com
 * @Description rcp调用auth微服务的用户权限服务
 * @create 2023/9/9
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DubboUserPermClient {

    @DubboReference(url = "dubbo://${dubbo.auth.host}:${dubbo.auth.port}", check = false)
    private UserPermService userPermService;

    public List<String> getPermissions(Long userId) {
        return userPermService.getPermByUserId(userId);
    }

    public List<String> getRoles(Long userId) {
        return userPermService.getRoleByUserId(userId);
    }
}
