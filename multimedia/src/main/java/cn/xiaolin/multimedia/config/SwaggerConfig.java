package cn.xiaolin.multimedia.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

/**
 * @author xingxiaolin xing.xiaolin@foxmail.com
 * @Description
 * @create 2023/8/5
 */

@OpenAPIDefinition(
        info = @Info(
                title = "文件视频接口API文档",
                description = "提供图像、视频上传下载的的基本操作",
                version = "0.9"
        )
)
@Configuration
public class SwaggerConfig {
}
