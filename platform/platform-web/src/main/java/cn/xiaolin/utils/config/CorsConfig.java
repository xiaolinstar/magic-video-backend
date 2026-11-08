package cn.xiaolin.utils.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;
import java.util.List;

/**
 * CORS configuration.
 *
 * NOTE: do NOT combine {@code AllowedOrigin("*")} with {@code allowCredentials(true)};
 * modern browsers will reject the response and Spring's permissive
 * {@code addAllowedOriginPattern("*")} historically lets any origin through with
 * credentials attached. Use an explicit allow-list instead.
 *
 * @author xingxiaolin xing.xiaolin@foxmail.com
 * @create 2023/8/19
 */
@Configuration
public class CorsConfig {

    /**
     * Comma-separated list of allowed origins. Override per environment, e.g.
     * {@code -Dmagic.cors.allowed-origins=https://app.example.com,https://admin.example.com}.
     * Defaults to a local dev origin so the app still starts without configuration.
     */
    @Value("${magic.cors.allowed-origins:http://localhost:5173,http://localhost:8080}")
    private String[] allowedOrigins;

    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();
        List<String> originList = Arrays.stream(allowedOrigins)
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toList();
        config.setAllowedOrigins(originList);
        // Allow the standard subset of methods; expand only if needed.
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        // Restrict request headers to what the API actually needs.
        config.setAllowedHeaders(List.of("Authorization", "Content-Type", "X-Requested-With", "sa-token"));
        // Surface the headers the frontend needs to read (e.g. sa-token).
        config.setExposedHeaders(List.of("sa-token"));
        // Allow cookies / Authorization to flow to the backend.
        config.setAllowCredentials(true);
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }
}
