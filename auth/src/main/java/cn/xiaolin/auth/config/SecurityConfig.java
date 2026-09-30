package cn.xiaolin.auth.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Spring Security configuration.
 *
 * Sa-Token is the primary authentication layer; Spring Security is included
 * only to provide the BCrypt {@link PasswordEncoder} bean. To make it explicit
 * and avoid surprising the next maintainer (Spring Security's default chain
 * would otherwise protect every endpoint), we register a no-op filter chain
 * that disables form login, HTTP basic, CSRF, and request matching.
 *
 * @author xingxiaolin xing.xiaolin@foxmail.com
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

    /**
     * Explicit permissive filter chain. Auth/authorization is delegated to
     * Sa-Token — see {@code cn.xiaolin.gateway.config.SaTokenConfig}.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .logout(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll());
        return http.build();
    }
}