package cn.xiaolin.gateway.config;

import cn.dev33.satoken.exception.DisableServiceException;
import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.exception.NotPermissionException;
import cn.dev33.satoken.exception.NotRoleException;
import cn.dev33.satoken.reactor.context.SaReactorSyncHolder;
import cn.dev33.satoken.reactor.filter.SaReactorFilter;
import cn.dev33.satoken.router.SaRouter;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaResult;
import cn.xiaolin.gateway.client.AuthUnavailableException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ServerWebExchange;

/**
 * Sa-Token gateway filter configuration.
 * Applies login check to all protected routes; public endpoints are excluded.
 *
 * @author xingxiaolin xing.xiaolin@foxmail.com
 * @create 2023/8/12
 */
@Configuration
@Slf4j
public class SaTokenConfig {

    /**
     * 免登录的公开路径白名单。
     *
     * <p>仅包含登录/注册等认证引导接口、swagger/openapi 文档以及 K8s 使用的 actuator 健康检查。
     * 注意：/auth/logout 与 /auth/kick-out 已刻意移出白名单——它们需要有效会话才能执行，
     * 也需要校验调用方是否有权强制其他用户下线。
     */
    private static final String[] PUBLIC_PATHS = {
            "/auth/login",
            "/auth/register",
            "/swagger-ui/**",
            "/swagger-resources/**",
            "/v3/api-docs/**",
            "/webjars/**",
            "/doc.html",
            "/actuator/health",
            "/actuator/health/**",
            "/actuator/info"
    };

    @Bean
    public SaReactorFilter getSaReactorFilter() {
        return new SaReactorFilter()
                // Apply auth check to all paths except the public whitelist
                .addInclude("/**")
                .addExclude(PUBLIC_PATHS)
                .setAuth(obj -> {
                    // Login check for every protected route
                    SaRouter.match("/**", StpUtil::checkLogin);
                    // Role-based check for sensitive sub-routes
                    SaRouter.match("/auth/user/**", r -> StpUtil.checkRole("common"));
                    SaRouter.match("/multimedia/**", r -> StpUtil.checkRole("common"));
                    SaRouter.match("/core/**", r -> StpUtil.checkRole("common"));
                    SaRouter.match("/has/**", r -> StpUtil.checkRole("common"));
                })
                .setError(e -> {
                    if (e instanceof NotLoginException) {
                        return SaResult.error("当前会话未登陆").setCode(401);
                    } else if (e instanceof NotRoleException) {
                        return SaResult.error("用户无访问角色").setCode(403);
                    } else if (e instanceof NotPermissionException) {
                        return SaResult.error("用户无访问权限").setCode(403);
                    } else if (e instanceof DisableServiceException) {
                        return SaResult.error("会话已封禁").setCode(403);
                    } else if (e instanceof AuthUnavailableException) {
                        ServerWebExchange exchange = SaReactorSyncHolder.getContext();
                        if (exchange != null) {
                            exchange.getResponse().setStatusCode(HttpStatus.SERVICE_UNAVAILABLE);
                        }
                        return SaResult.error("认证服务不可用").setCode(503);
                    }
                    // Avoid leaking raw exception messages for unknown errors
                    log.warn("Sa-Token filter unexpected error", e);
                    return SaResult.error("认证服务异常").setCode(500);
                });
    }
}
