package cn.xiaolin.auth.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

/**
 * @author xingxiaolin xing.xiaolin@foxmail.com
 * @Description 用于配置Swagger文档的生成，提供基本信息和API描述
 * @create 2023/8/12
 */

@OpenAPIDefinition(
        info = @Info(
                title = "权限中台接口API文档",
                description = "提供对权限资源的基本操作，对外提供Restful和RPC接口",
                version = "0.9"
        )
)
@Configuration
public class SwaggerConfig {
}
