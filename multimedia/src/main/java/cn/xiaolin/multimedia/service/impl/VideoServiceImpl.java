package cn.xiaolin.multimedia.service.impl;

import cn.xiaolin.multimedia.config.MinioConfigProperties;
import cn.xiaolin.multimedia.domain.dto.SliceFileUploadRequestDto;
import cn.xiaolin.multimedia.service.VideoService;
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
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentMap;

/**
 * @author xingxiaolin xing.xiaolin@foxmail.com
 * @Description 视频服务实现类
 * @create 2023/7/23
 */
@Service
@EnableConfigurationProperties(MinioConfigProperties.class)
public class VideoServiceImpl implements VideoService {

    private final MinioClient minioClient;
    private final String videoBucketName;


    public VideoServiceImpl(MinioClient minioClient, MinioConfigProperties minioConfigProperties) {
        this.minioClient = minioClient;
        this.videoBucketName = minioConfigProperties.getVideo().getBucketName();
    }

    /**
     * 文件上传
     *
     * @param video MultipartFile格式文件
     * @return 文件id
     */
    @Override
    public String videoUpload(MultipartFile video) {
        if (video.isEmpty()) {
            throw new GlobalException("视频内容为空");
        } else if (!Objects.equals(video.getContentType(), "video/mp4")) {
            throw new GlobalException("视频格式必须为video/mp4");
        }

        try (BufferedInputStream inputStream = new BufferedInputStream(video.getInputStream())) {
            PutObjectArgs putObjectArgs = PutObjectArgs.builder()
                    .bucket(videoBucketName)
                    .object(video.getOriginalFilename())
                    .stream(inputStream, video.getSize(), -1)
                    .contentType("video/mp4")
                    .build();
            minioClient.putObject(putObjectArgs);

            return minioClient.getPresignedObjectUrl(GetPresignedObjectUrlArgs.builder()
                    .bucket(videoBucketName)
                    .method(Method.GET)
                    .object(video.getOriginalFilename())
                    .build()
            );
        } catch (IOException | ErrorResponseException | InsufficientDataException | InternalException |
                 InvalidKeyException | InvalidResponseException | NoSuchAlgorithmException | ServerException |
                 XmlParserException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Boolean initSliceUpload(String md5, long chunkNum) {
        return null;
    }

    @Override
    public long sliceVideoUpload(SliceFileUploadRequestDto requestDTO) {
        return 0L;
    }


    @Override
    public Boolean abortUpload(String md5) {
        return Boolean.FALSE;
    }

    @Override
    public String sliceVideoMerge(String md5) {
        return "";
    }

}
