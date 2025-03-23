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


/**
 * @author xingxiaolin xing.xiaolin@foxmail.com
 * @Description SaToken配置门闩
 * @create 2023/8/12
 */
@Configuration
@Slf4j
public class SaTokenConfig{

    @Bean
    public SaReactorFilter getSaReactorFilter() {
        return new SaReactorFilter()
                .addInclude("/**")
                .addExclude("/auth/login", "/auth/register", "/auth/logout", "/swagger-ui.html")
                .setAuth(obj -> {
                    System.out.println("---------- 进入Sa-Token全局认证 -----------");
                    // 登陆校验：校验除登陆路由外的所有路由
                    SaRouter.match("/**", StpUtil::checkLogin);
                    // 权限校验
                    SaRouter.match("/auth/user/**", r -> StpUtil.checkRole("common"));
                    SaRouter.match("/multimedia/**", r -> StpUtil.checkRole("common"));
                    SaRouter.match("/core/**", r -> StpUtil.checkRole("common"));

                })
                .setError(e ->{
                    if (e instanceof NotLoginException) {
                        return SaResult.error("当前会话未登陆").setCode(401);
                    } else if (e instanceof NotRoleException) {
                        return SaResult.error("用户无访问角色").setCode(403);
                    } else if (e instanceof NotPermissionException) {
                        return SaResult.error("用户无访问权限").setCode(403);
                    } else if (e instanceof DisableServiceException) {
                        return SaResult.error("会话已封禁").setCode(403);
                    }
                    return SaResult.error(e.getMessage());
                });
    }




//    @Bean
//    public SaReactorFilter getSaReactorFilter() {
//        // 关闭权限校验
//        return new SaReactorFilter()
//                .addExclude("/**")
//                .setError(e -> SaResult.error(e.getMessage()));
//    }
}
