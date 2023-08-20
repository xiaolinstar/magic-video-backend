package cn.xiaolin.auth.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

/**
 * @author xingxiaolin xlxing@bupt.edu.cn
 * @Description
 * @create 2023/8/12
 */

@OpenAPIDefinition(
        info = @Info(
                title = "权限中台接口API文档",
                description = "提供对权限资源的基本操作，对外提供Restful和gRPC接口",
                version = "0.9"
        )
)
@Configuration
public class SwaggerConfig {
}
