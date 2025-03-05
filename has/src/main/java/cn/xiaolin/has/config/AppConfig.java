package cn.xiaolin.has.config;

import cn.xiaolin.utils.exception.GlobalException;
import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.SetBucketPolicyArgs;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.bramp.ffmpeg.FFmpeg;
import net.bramp.ffmpeg.FFprobe;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

/**
 * @author xingxiaolin xing.xiaolin@foxmail.com
 * @Description has微服务第三方Bean
 * @create 2024/11/11
 */
@Slf4j
@Configuration
@EnableConfigurationProperties({MinioConfigProperties.class, AppConfigProperties.class})
@RequiredArgsConstructor
public class AppConfig implements CommandLineRunner {

    private final MinioConfigProperties minioConfigProperties;
    private final AppConfigProperties appConfigProperties;

    @Bean
    public FFmpeg ffmpeg() throws IOException {
        return new FFmpeg();
    }

    @Bean
    public FFprobe fFprobe() throws IOException {
        return new FFprobe();
    }

    /**
     * 初始化Minio客户端
     * TODO 桶权限需设置为public
     * TODO 视频资源m3u8、ts等过期时间为永久
     * @return minio客户端
     */
    @Bean
    public MinioClient minioClient() {
        MinioClient minioClient = MinioClient.builder()
                .endpoint(minioConfigProperties.getEndpoint())
                .credentials(minioConfigProperties.getAccessKey(), minioConfigProperties.getSecretKey())
                .build();
        List<String> bucketNameList = List.of(
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

                // 设置存储桶策略为公共可读
                String policy = "{\"Version\":\"2012-10-17\",\"Statement\":[{\"Effect\":\"Allow\",\"Principal\":{\"AWS\":[\"*\"]},\"Action\":[\"s3:GetBucketLocation\",\"s3:ListBucket\"],\"Resource\":[\"arn:aws:s3:::" + bucketName + "\"]},{\"Effect\":\"Allow\",\"Principal\":{\"AWS\":[\"*\"]},\"Action\":[\"s3:GetObject\"],\"Resource\":[\"arn:aws:s3:::" + bucketName + "/*\"]}]}";
                minioClient.setBucketPolicy(SetBucketPolicyArgs.builder()
                        .bucket(bucketName)
                        .config(policy)
                        .build());
                log.info("bucket: {} 已设置为公共可读", bucketName);

            } catch (Exception e) {
                throw new GlobalException(e.getMessage());
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
        String hlsFileDir = appConfigProperties.getHlsFileDir();
        log.info("当前主机 home 目录: {}", homeDir);
        Path hlsPath = Path.of(homeDir, hlsFileDir);
        File hlsFile = hlsPath.toFile();
        if (!hlsFile.exists() && !hlsFile.mkdirs()) {
            log.info("创建视频文件目录成功：{}", hlsPath);
        } else {
            log.info("视频文件目录已存在：{}", hlsPath);
        }
    }
}
