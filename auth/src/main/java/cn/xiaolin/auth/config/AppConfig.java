package cn.xiaolin.auth.config;

import cn.xiaolin.auth.domain.dto.SysUserReqDto;
import cn.xiaolin.auth.domain.entity.SysUser;
import cn.xiaolin.auth.service.SysUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Optional;

/**
 * @author xingxiaolin xing.xiaolin@foxmail.com
 * @Description 应用启动配置
 * @create 2025/3/23
 */
@Configuration
@Slf4j
@RequiredArgsConstructor
public class AppConfig implements CommandLineRunner {
    private final SysUserService sysUserService;

    @Override
    public void run(String... args) throws Exception {
        log.info("Auth微服务启动配置...");
        log.info("检查密码加密情况，并对未加密密码进行加密");
        List<SysUser> sysUserList = sysUserService.list();

        for (SysUser user : sysUserList) {
            if (user.getPassword().startsWith("$2a$")) {
                log.info("用户名{}：密码已加密", user.getUsername());
            } else {
                Optional<SysUser> sysUser = sysUserService.updateAndReturn(
                        SysUserReqDto.builder()
                                .id(user.getId())
                                .password(user.getPassword())
                                .build()
                );
                log.info("用户名{}：密码加密成功", sysUser.get().getUsername());
            }
        }
    }
}
