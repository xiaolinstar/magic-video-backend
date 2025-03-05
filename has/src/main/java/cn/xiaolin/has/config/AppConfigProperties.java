package cn.xiaolin.has.config;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author xingxiaolin xing.xiaolin@foxmail.com
 * @Description
 * @create 2025/3/2
 */
@ConfigurationProperties(prefix = "app")
@Getter
@Setter
@NoArgsConstructor
public class AppConfigProperties {
    // HLS视频文件夹
    private String hlsFileDir;

    // DASH视频文件夹
    private String dashFileDir;
}
