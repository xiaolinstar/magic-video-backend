package cn.xiaolin.gateway.config;

import cn.dev33.satoken.exception.DisableServiceException;
import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.exception.NotPermissionException;
import cn.dev33.satoken.exception.NotRoleException;
import cn.dev33.satoken.reactor.filter.SaReactorFilter;
import cn.dev33.satoken.router.SaRouter;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

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
     * Paths that bypass authentication (public endpoints).
     * Includes auth flow, swagger/openapi, and actuator health probes used by K8s.
     */
    private static final String[] PUBLIC_PATHS = {
            "/auth/login",
            "/auth/register",
            "/auth/logout",
            "/auth/kick-out",
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
                .addExclude(List.of(PUBLIC_PATHS))
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
                    }
                    // Avoid leaking raw exception messages for unknown errors
                    log.warn("Sa-Token filter unexpected error", e);
                    return SaResult.error("认证服务异常").setCode(500);
                });
    }
}
