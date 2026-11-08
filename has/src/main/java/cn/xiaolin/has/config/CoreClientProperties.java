package cn.xiaolin.has.config;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Core service location used by {@code CoreResourceClient}.
 */
@ConfigurationProperties(prefix = "core")
@Getter
@Setter
@NoArgsConstructor
public class CoreClientProperties {

    /**
     * Core origin without a path. Compose default is {@code http://magic-core:9001}.
     */
    private String baseUrl;
}
