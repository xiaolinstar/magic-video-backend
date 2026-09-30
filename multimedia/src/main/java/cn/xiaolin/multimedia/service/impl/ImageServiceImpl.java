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
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.NotImplementedException;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;
import java.util.regex.Pattern;

/**
 * @author xingxiaolin xing.xiaolin@foxmail.com
 * @create 2023/7/23
 */
@Service
@Slf4j
@RequiredArgsConstructor
@EnableConfigurationProperties(MinioConfigProperties.class)
public class ImageServiceImpl implements ImageService {

    /**
     * Allowed object-key suffixes. Anything else is rejected so that user-supplied
     * filenames cannot inject path separators or overwrite sibling objects in MinIO.
     */
    private static final Pattern SAFE_SUFFIX = Pattern.compile("^[a-zA-Z0-9]{1,8}$");

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
        }
        log.info("Image contentType: {}", image.getContentType());

        // Derive an object key from a UUID plus a sanitized extension. We never use
        // the user-controlled originalFilename as the MinIO object key — that would
        // let an attacker inject "/" or "../" segments and overwrite sibling objects.
        String safeObjectKey = buildSafeObjectKey(image.getOriginalFilename());

        try (BufferedInputStream inputStream = new BufferedInputStream(image.getInputStream())) {
            PutObjectArgs putObjectArgs = PutObjectArgs.builder()
                    .bucket(getBucketName())
                    .object(safeObjectKey)
                    .stream(inputStream, image.getSize(), -1)
                    .contentType(image.getContentType())
                    .build();
            minioClient.putObject(putObjectArgs);
            return minioClient.getPresignedObjectUrl(GetPresignedObjectUrlArgs.builder()
                    .bucket(getBucketName())
                    .method(Method.GET)
                    .object(safeObjectKey)
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

    /**
     * Build a deterministic, sanitized MinIO object key from the original filename.
     * Only the alphanumeric portion of the extension is preserved; everything else
     * (path, special characters, leading dots) is stripped.
     */
    private String buildSafeObjectKey(String originalFilename) {
        String suffix = "";
        if (originalFilename != null) {
            int dot = originalFilename.lastIndexOf('.');
            if (dot >= 0 && dot < originalFilename.length() - 1) {
                String ext = originalFilename.substring(dot + 1);
                // strip everything after the first non-alphanumeric character
                StringBuilder clean = new StringBuilder();
                for (int i = 0; i < ext.length() && clean.length() < 8; i++) {
                    char c = ext.charAt(i);
                    if (Character.isLetterOrDigit(c)) {
                        clean.append(Character.toLowerCase(c));
                    } else {
                        break;
                    }
                }
                if (clean.length() > 0 && SAFE_SUFFIX.matcher(clean).matches()) {
                    suffix = "." + clean;
                }
            }
        }
        return UUID.randomUUID().toString().replace("-", "") + suffix;
    }
}
