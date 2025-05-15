package cn.xiaolin.has.service;

import cn.xiaolin.has.config.MinioConfigProperties;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MinioClient;
import io.minio.errors.*;
import io.minio.http.Method;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * @author xingxiaolin xing.xiaolin@foxmail.com
 * @Description
 * @create 2025/3/2
 */

@SpringBootTest
@ActiveProfiles("dev")
public class MediaKitServiceTest {
    @Autowired
    private MediaKitService mediaKitService;

    @Autowired
    private MinioConfigProperties minioConfigProperties;

    @Autowired
    private MinioClient minioClient;

    @Test
    public void testMedia2Hls() {
        // 1. 从 multimedia-minio 下载 mp4 视频
        // 2. 使用 ffmpeg 将 mp4 视频转 hls 视频
        // 3. 将 hls 视频上传到 has-minio
        String videoUrl = "http://localhost:9010/magic-video/Md5-Otis-Ruby.mp4?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Credential=RzQywv1gIcgXeSSMMeNz%2F20250305%2Fus-east-1%2Fs3%2Faws4_request&X-Amz-Date=20250305T131959Z&X-Amz-Expires=604800&X-Amz-SignedHeaders=host&X-Amz-Signature=98fa87f5c11277dadf8a3e346b829bbb3cc45b9c4b3fab961ffb87af0337c3cd";
        mediaKitService.media2Hls(videoUrl);
    }

    @Test
    public void testMedia2Dash() {
        String videoUrl = "http://localhost:9010/magic-video/Otis%20%26%20Ruby.mp4?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Credential=RzQywv1gIcgXeSSMMeNz%2F20250407%2Fus-east-1%2Fs3%2Faws4_request&X-Amz-Date=20250407T142936Z&X-Amz-Expires=604800&X-Amz-SignedHeaders=host&X-Amz-Signature=ac53b7d0ed0ae76f453d20661cab599c87ef78b69bc9c8e63a42f2e5b8b9f4d8";
        mediaKitService.media2Dash(videoUrl);
    }

    @Test
    public void testGetHlsUrl() throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        String videoName = "Md5-Otis-Ruby.m3u8";
        // Bucket桶策略需为公开访问 public
        GetPresignedObjectUrlArgs objectUrlArgs = GetPresignedObjectUrlArgs.builder()
                .bucket(minioConfigProperties.getHls().getBucketName())
                .object(videoName)
                .method(Method.GET)
                .build();
        String url = minioClient.getPresignedObjectUrl(objectUrlArgs);
        System.out.println(url);
        assertNotNull(url);
    }
    @Test
    public void testGetDashUrl() throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        String videoName = "Otis%20%26%20Ruby/Otis%20%26%20Ruby.mpd";
        // Bucket桶策略需为公开访问 public
        GetPresignedObjectUrlArgs objectUrlArgs = GetPresignedObjectUrlArgs.builder()
                .bucket(minioConfigProperties.getDash().getBucketName())
                .object(videoName)
                .method(Method.GET)
                .build();
        String url = minioClient.getPresignedObjectUrl(objectUrlArgs);
        System.out.println(url);
        assertNotNull(url);
    }


}
