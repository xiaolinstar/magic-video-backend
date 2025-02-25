package cn.xiaolin.multimedia.service.impl;

import cn.xiaolin.multimedia.config.MinioConfigProperties;
import cn.xiaolin.multimedia.enums.VideoTypeEnum;
import cn.xiaolin.multimedia.service.VideoService;
import cn.xiaolin.utils.exception.GlobalException;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.errors.*;
import io.minio.http.Method;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Path;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

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
    // TODO 视频文件存储目录，使用配置参数定义，在项目启动时创建
    private final static String videoFileDir = "/Users/xlxing/Documents/magic-video";


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

    /**
     * 视频分片上传
     * 实现步骤概述：
     *  1 定义目录md5，文件名chunkId，目标文件 videoFileDir/md5/chunkId
     *  2.1 查询是否已经存在，如果存在，直接返回下一个chunkId
     *  2.2 如果不存在，创建文件名，上传文件，返回下一个chunkId
     * @param chunkVideo 分片文件
     * @param md5 视频标识
     * @param chunkId 分片序号
     * @param chunkMd5 分片摘要值
     * @return 下一个分片序号
     */
    @Override
    public Long uploadVideoChunk(MultipartFile chunkVideo, String md5, long chunkId, String chunkMd5) {
        Path dirPath = Path.of(videoFileDir, md5);
        File dirFile = dirPath.toFile();
        // 1. 创建文件目录：不存在时创建
        if (!dirFile.exists()) {
            if (!dirFile.mkdir()) {
                throw new GlobalException("创建文件夹失败：" + dirPath);
            }
        }

        // 2. 创建文件：不存在时创建
        Path chunkPath = Path.of(dirPath.toString(), String.valueOf(chunkId));
        File chunkFile = chunkPath.toFile();
        if (!chunkFile.exists()) {
            try (InputStream inputStream = chunkVideo.getInputStream();
                 FileOutputStream outputStream = new FileOutputStream(chunkFile)
            ) {
                // 2.1 写目标文件
                byte[] bytes = new byte[4096];
                int bytesRead;
                while ((bytesRead = inputStream.read(bytes)) != -1) {
                    outputStream.write(bytes, 0, bytesRead);
                }
            } catch (IOException e) {
                throw new GlobalException("上传分片失败：" + e.getMessage());
            }

        }
        // 2.2 返回下一个分片序号
        return chunkId + 1;

    }


    @Override
    public Boolean abortUpload(String md5) {
        return Boolean.FALSE;
    }

    /**
     * 视频分片合并
     * 实现步骤概述：
     *  1 创建目标文件：videoFileDir/md5+videoType
     *  2 查询 videoFileDir/md5 目录下的所有分片，排序后合并
     *  3 校验合并后的文件摘要值，是否一致
     * @param md5 视频标识
     * @param videoType 视频类型
     */
    @Override
    public void videoChunksMerge(String md5, VideoTypeEnum videoType) {
        Path dirPath = Path.of(videoFileDir, md5);
        File dirFile = dirPath.toFile();
        if (!dirFile.exists()) {
            throw new GlobalException("文件目录不存在 " + dirPath);
        }

        // 创建目标文件：videoFileDir/md5+videoType
        Path targetPath = Path.of(videoFileDir, md5 + ".mp4");
        File targetFile = targetPath.toFile();
        if (!targetFile.exists()) {
            List<Path> chunkPathList = getChunkFiles(dirFile);
            try (FileOutputStream outputStream = new FileOutputStream(targetFile)) {
                chunkPathList.forEach(chunkPath -> {
                    try (FileInputStream inputStream = new FileInputStream(chunkPath.toFile())) {
                        byte[] bytes = new byte[4096];
                        int bytesRead;
                        while ((bytesRead = inputStream.read(bytes)) != -1) {
                            outputStream.write(bytes, 0, bytesRead);
                        }
                    } catch (IOException e) {
                        throw new GlobalException("合并分片失败：" + e.getMessage());
                    }
                });
            } catch (IOException e) {
                throw new GlobalException("合并分片失败：" + e.getMessage());
            }
        }
    }
    private List<Path> getChunkFiles(File dirFile) {
        String[] chunkNames = dirFile.list();
        if (chunkNames == null || chunkNames.length == 0) {
            throw new GlobalException("文件目录为空 " + dirFile);
        }
        // 文件序号排序
        Arrays.sort(chunkNames, Comparator.comparingInt(Integer::parseInt));
        return Stream.of(chunkNames)
                .map(name -> Path.of(dirFile.getPath(), name))
                .toList();
    }
}
