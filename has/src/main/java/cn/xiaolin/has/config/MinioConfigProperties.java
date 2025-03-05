package cn.xiaolin.has.config;

import lombok.*;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author xingxiaolin xing.xiaolin@foxmail.com
 * @Description 对象存储Minio相关配置信息
 * @create 2025/3/2
 */

@ConfigurationProperties(prefix = "minio")
@Getter
@Setter
@NoArgsConstructor
public class MinioConfigProperties {

    private String endpoint;
    private String accessKey;
    private String secretKey;

    private HlsProperties hls = new HlsProperties();
    private DashProperties dash = new DashProperties();



    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class HlsProperties {
        private String bucketName;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DashProperties {
        private String bucketName;
    }

}
