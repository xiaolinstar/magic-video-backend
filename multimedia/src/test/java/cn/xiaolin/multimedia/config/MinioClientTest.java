package cn.xiaolin.multimedia.config;

import io.minio.*;
import io.minio.errors.*;
import io.minio.http.Method;
import io.minio.messages.Bucket;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.UrlResource;
import org.springframework.test.context.ActiveProfiles;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
/**
  *@author      xingxiaolin xing.xiaolin@foxmail.com
  *@Description
  *@create      2024/10/11
 * */

@SpringBootTest
@ActiveProfiles("dev")
class MinioClientTest {

    @Autowired
    private MinioClient minioClient;

    @Test
    public void minioClientConnect() throws ServerException, InsufficientDataException,
            ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException,
            InvalidResponseException, XmlParserException, InternalException {
        List<String> buckets = minioClient.listBuckets()
                .stream()
                .map(Bucket::name)
                .toList();
        List<String> bucketNameList = List.of("magic-image", "magic-video");
        bucketNameList.forEach(bucketName -> {
            assertTrue(buckets.contains(bucketName));
        });
    }

    @Test
    public void minioCreateRemoveBucket() throws ServerException, InsufficientDataException,
            ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException,
            InvalidResponseException, XmlParserException, InternalException {

        String bucketName = "test-image";
        if (minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build())) {
            System.out.printf("Bucket %s already exists.\n", bucketName);

        } else {
            minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
            System.out.printf("Bucket %s created successfully.\n", bucketName);
        }
        assertTrue(minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build()));

        minioClient.removeBucket(RemoveBucketArgs.builder()
                .bucket(bucketName)
                .build());
        assertFalse(minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build()));
    }

    @Test
    public void minioAddObject() throws ServerException, InsufficientDataException,
            ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException,
            InvalidResponseException, XmlParserException, InternalException {
        String bucketName = "test-image";
        String objectName = "sparrow.svg";

        if (!minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build())) {
            minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
            assertTrue(minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build()));
        }

        String url = "https://www.xiaolin.fun/sparrow.svg";
        URL sparrowUrl = new URL(url);
        URLConnection urlConnection = sparrowUrl.openConnection();

        PutObjectArgs objectArgs = PutObjectArgs.builder()
                .bucket(bucketName)
                .object("sparrow.svg")
                .contentType(urlConnection.getContentType())
                .stream(new BufferedInputStream(urlConnection.getInputStream()),
                        urlConnection.getContentLengthLong(),
                        -1)
                .build();
        minioClient.putObject(objectArgs);

        String objectUrl = minioClient.getPresignedObjectUrl(GetPresignedObjectUrlArgs.builder()
                .bucket(bucketName)
                .object(objectName)
                .method(Method.GET)
                .build()
        );
        assertTrue(objectUrl.contains("sparrow.svg"));

        minioClient.removeObject(RemoveObjectArgs.builder()
                .bucket(bucketName)
                .object(objectName)
                .build());
        minioClient.removeBucket(RemoveBucketArgs.builder().bucket(bucketName).build());
        assertFalse(minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build()));

    }

    @Test
    public void minioVideoList() throws ServerException, InsufficientDataException, ErrorResponseException, IOException,
            NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        String bucketName = "magic-video";
        String objectName = "Otis & Ruby.mp4";
        GetPresignedObjectUrlArgs objectUrlArgs = GetPresignedObjectUrlArgs.builder()
                .bucket(bucketName)
                .object(objectName)
                .method(Method.GET)
                .build();
        String url = minioClient.getPresignedObjectUrl(objectUrlArgs);
        System.out.println(url);
    }

    @Test
    public void testDownload() throws ServerException, InsufficientDataException, ErrorResponseException, IOException,
            NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        String bucketName = "magic-video";
        String objectName = "Otis & Ruby.mp4";
        GetObjectArgs getObjectArgs = GetObjectArgs.builder()
                .bucket(bucketName)
                .object(objectName)
                .build();
        GetObjectResponse response = minioClient.getObject(getObjectArgs);
        // url拼接出bucket和object时需要进行字符串反转义

        // 下载视频存储到本地
        response.transferTo(new FileOutputStream(new File("./Otis & Ruby.mp4")));
    }

    @Test
    public void testURL() throws MalformedURLException {
        String url = "http://localhost:9010/magic-video/Otis%20%26%20Ruby.mp4?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Credential=RzQywv1gIcgXeSSMMeNz%2F20241115%2Fus-east-1%2Fs3%2Faws4_request&X-Amz-Date=20241115T061735Z&X-Amz-Expires=604800&X-Amz-SignedHeaders=host&X-Amz-Signature=2cd5ad0a3ab5b813623c90ed494c177f4c15cdf73f32ddfe006a6130e4b61d4e";
        URL u = new URL(url);
        System.out.println(u.getHost());
        String path = u.getPath();
        String[] strings = path.split("/");
        List.of(strings).forEach(System.out::println);
    }

}