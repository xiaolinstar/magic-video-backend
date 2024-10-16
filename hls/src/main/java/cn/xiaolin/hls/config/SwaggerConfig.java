package cn.xiaolin.hls.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;

/**
 * @author xingxiaolin xing.xiaolin@foxmail.com
 * @Description
 * @create 2024/10/13
 */

@OpenAPIDefinition(
        info = @Info(
                title = "Http Adaptive Streaming接口API文档",
                description = "视频转码为http自适应流：hls dash",
                version = "0.9"
        )
)
public class SwaggerConfig {
}
