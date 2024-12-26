package cn.xiaolin.multimedia.config;

import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author xingxiaolin xing.xiaolin@foxmail.com
 * @Description 媒体处理配置
 * @create 2023/7/29
 */
@Configuration
@EnableConfigurationProperties(MinioConfigProperties.class)
@RequiredArgsConstructor
public class AppConfig {
    private final MinioConfigProperties minioConfigProperties;

    @Bean
    public ConcurrentMap<String, Long> resourceIdMap() {
        return new ConcurrentHashMap<>();
    }

    /**
     * 对象文件存储minio
     * 注册minioClient Bean时初始化bucket
     */
    @Bean
    public MinioClient minioClient() {

        MinioClient minioClient = MinioClient.builder()
                .endpoint(minioConfigProperties.getEndpoint())
                .credentials(minioConfigProperties.getAccessKey(), minioConfigProperties.getSecretKey())
                .build();
        List<String> bucketNameList = List.of(
                minioConfigProperties.getImage().getBucketName(),
                minioConfigProperties.getVideo().getBucketName(),
                minioConfigProperties.getHls().getBucketName(),
                minioConfigProperties.getDash().getBucketName()
        );

        bucketNameList.forEach(bucketName -> {
            try {
                if (!minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build())) {
                    minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        return minioClient;
    }


}
