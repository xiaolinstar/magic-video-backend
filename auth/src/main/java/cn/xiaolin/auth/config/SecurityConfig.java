package cn.xiaolin.auth.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * @author xingxiaolin xing.xiaolin@foxmail.com
 * @Description 定义一个 Spring 配置类，用于配置密码编码器
 * @create 2023/8/12
 */
@Configuration
public class SecurityConfig {

    /**
     * 密码加密
     * @return encoder
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
