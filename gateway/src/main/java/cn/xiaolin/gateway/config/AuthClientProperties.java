package cn.xiaolin.gateway.config;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Auth service location used by the gateway permission client.
 */
@Component
@ConfigurationProperties(prefix = "auth")
@Getter
@Setter
@NoArgsConstructor
public class AuthClientProperties {

    /**
     * Auth origin without a path. Local compose default is {@code http://magic-auth:8999}.
     */
    private String baseUrl;
}
