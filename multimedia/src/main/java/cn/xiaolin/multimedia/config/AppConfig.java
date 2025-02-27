package cn.xiaolin.multimedia.config;

import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author xingxiaolin xing.xiaolin@foxmail.com
 * @Description 媒体处理配置
 * @create 2023/7/29
 */
@Slf4j
@Configuration
@EnableConfigurationProperties({MinioConfigProperties.class, AppConfigProperties.class})
@RequiredArgsConstructor
public class AppConfig implements CommandLineRunner {
    private final MinioConfigProperties minioConfigProperties;
    private final AppConfigProperties appConfigProperties;

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
                    log.info("创建bucket: {}", bucketName);
                    minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
                } else {
                    log.info("bucket: {} 已存在", bucketName);
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        return minioClient;
    }


    /**
     * 项目启动时创建视频文件存储目录
     */
    @Override
    public void run(String... args) {
        log.info("项目启动，自定义初始化...");
        String homeDir = System.getProperty("user.home");
        String videoDir = appConfigProperties.getVideoFileDir();
        log.info("当前主机 home 目录: {}", homeDir);
        Path videoPath = Path.of(homeDir, videoDir);
        File videoFile = videoPath.toFile();
        if (!videoFile.exists() && !videoFile.mkdirs()) {
            log.info("创建视频文件目录成功：{}", videoPath);
        } else {
            log.info("视频文件目录已存在：{}", videoPath);
        }
    }
}
