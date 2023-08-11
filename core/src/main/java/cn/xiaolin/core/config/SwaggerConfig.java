package cn.xiaolin.core.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.context.annotation.Configuration;

/**
 * @author xingxiaolin xlxing@bupt.edu.cn
 * @Description
 * @create 2023/8/5
 */

@OpenAPIDefinition(
        info = @Info(
                title = "视频点播接口API文档",
                description = "提供对视频资源的基本操作",
                version = "0.9"
        )
)
@Configuration
public class SwaggerConfig {
}
