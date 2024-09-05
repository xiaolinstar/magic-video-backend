package cn.xiaolin.multimedia.config;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author xingxiaolin xing.xiaolin@foxmail.com
 * @Description App相关配置信息
 * @create 2023/8/26
 */
@ConfigurationProperties(prefix = "app", ignoreInvalidFields = true)
@Getter
@Setter
@NoArgsConstructor
public class AppConfigProperties {
    private Integer coreSize;
    private Integer maxPoolSize;
    private Integer queueCapacity;
    private String threadPrefix;
}
