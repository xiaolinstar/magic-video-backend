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

import java.io.BufferedInputStream;
import java.io.IOException;
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


}