package cn.xiaolin.gateway.config;

import cn.dev33.satoken.reactor.filter.SaReactorFilter;
import cn.dev33.satoken.router.SaRouter;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaResult;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author xingxiaolin xing.xiaolin@foxmail.com
 * @Description SaToken配置门闩
 * @create 2023/8/12
 */
@Configuration
public class SaTokenConfig {

    @Bean
    public SaReactorFilter getSaReactorFilter() {
        return new SaReactorFilter()
                .addInclude("/**")
                .addExclude("/auth/login", "/auth/register", "/auth/logout", "/swagger-ui.html")
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

//    @Bean
//    public SaReactorFilter getSaReactorFilter() {
//        // 关闭权限校验
//        return new SaReactorFilter()
//                .addExclude("/**")
//                .setError(e -> SaResult.error(e.getMessage()));
//    }
}
