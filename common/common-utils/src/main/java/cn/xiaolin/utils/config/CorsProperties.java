package cn.xiaolin.utils.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author xingxiaolin xlxing@bupt.edu.cn
 * @Description 跨域配置参数
 * @create 2023/8/23
 */

@ConfigurationProperties(prefix = "cors")
@Data
public class CorsProperties {

    private String uri;
}
