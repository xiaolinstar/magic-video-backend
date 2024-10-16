package cn.xiaolin.multimedia.config;

import lombok.*;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
  *@author      xingxiaolin xing.xiaolin@foxmail.com
  *@Description      
  *@create      2024/10/11
  */

@ConfigurationProperties(prefix = "minio")
@Getter
@Setter
@NoArgsConstructor
public class MinioConfigProperties {
    private String endpoint;
    private String accessKey;
    private String secretKey;
    private ImageProperties image = new ImageProperties();
    private VideoProperties video = new VideoProperties();
    private HlsProperties hls = new HlsProperties();
    private DashProperties dash = new DashProperties();

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ImageProperties {
        private String bucketName;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class VideoProperties {
        private String bucketName;
    }


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
