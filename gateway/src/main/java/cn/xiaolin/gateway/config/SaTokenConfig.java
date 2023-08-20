package cn.xiaolin.gateway.config;

import cn.dev33.satoken.reactor.filter.SaReactorFilter;
import cn.dev33.satoken.router.SaRouter;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaResult;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author xingxiaolin xlxing@bupt.edu.cn
 * @Description
 * @create 2023/8/12
 */
@Configuration
public class SaTokenConfig {

    @Bean
    public SaReactorFilter getSaReactorFilter() {
        return new SaReactorFilter()
                .addInclude("/**")
                .addExclude("/auth/login", "/auth/register", "/auth/logout")
                .setAuth(obj -> {
                    // 登陆校验：校验除登陆路由外的所有路由
                    SaRouter.match("/**",  r -> StpUtil.checkLogin());
                    // 权限校验
                    SaRouter.match("/auth/user/**", r -> StpUtil.checkRole("user"));
                    SaRouter.match("/multimedia/**", r -> StpUtil.checkRole("admin"));
                    SaRouter.match("/core/**", r -> StpUtil.checkRole("user"));
                })
                .setError(e -> SaResult.error(e.getMessage()));
    }
}
