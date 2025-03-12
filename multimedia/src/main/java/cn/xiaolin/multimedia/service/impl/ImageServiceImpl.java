package cn.xiaolin.multimedia.service.impl;

import cn.xiaolin.multimedia.config.MinioConfigProperties;
import cn.xiaolin.multimedia.service.ImageService;
import cn.xiaolin.utils.exception.GlobalException;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.errors.*;
import io.minio.http.Method;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.NotImplementedException;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;

/**
 * @author xingxiaolin xing.xiaolin@foxmail.com
 * @Description
 * @create 2023/7/23
 */
@Service
@RequiredArgsConstructor
@EnableConfigurationProperties(MinioConfigProperties.class)
public class ImageServiceImpl implements ImageService {

    private final MinioClient minioClient;
    private final MinioConfigProperties minioConfigProperties;

    protected String getBucketName() {
        return minioConfigProperties.getImage().getBucketName();
    }

    /**
     * 图像上传
     *
     * @param image 图像
     * @return 图像资源 Url
     */
    @Override
    public String imageUpload(MultipartFile image) {
        if (image.isEmpty()) {
            throw new GlobalException("图像内容为空");
        } else if (!Objects.equals(image.getContentType(), "image/jpeg")) {
            throw new GlobalException("图像格式必须为image/jpeg");
        }

        try (BufferedInputStream inputStream = new BufferedInputStream(image.getInputStream())) {
            PutObjectArgs putObjectArgs = PutObjectArgs.builder()
                    .bucket(getBucketName())
                    .object(image.getOriginalFilename())
                    .stream(inputStream, image.getSize(), -1)
                    .contentType("image/jpeg")
                    .build();
            minioClient.putObject(putObjectArgs);
            return minioClient.getPresignedObjectUrl(GetPresignedObjectUrlArgs.builder()
                    .bucket(getBucketName())
                    .method(Method.GET)
                    .object(image.getOriginalFilename())
                    .build()
            );
        } catch (IOException | ErrorResponseException | InsufficientDataException | InternalException |
                 InvalidKeyException | InvalidResponseException | NoSuchAlgorithmException | ServerException |
                 XmlParserException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 字符串加密上传文件
     *
     * @param imageBase64 图像的64位加密
     * @return 图像资源ID
     */
    @Override
    public String imageUpload(String imageBase64) {
        throw new NotImplementedException();
    }


}
