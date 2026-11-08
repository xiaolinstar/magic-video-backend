package cn.xiaolin.multimedia.service.impl;

import cn.xiaolin.multimedia.mq.MessageProducer;
import cn.xiaolin.multimedia.mq.TranscodeMessage;
import cn.xiaolin.multimedia.enums.VideoTypeEnum;
import cn.xiaolin.multimedia.service.VideoService;
import cn.xiaolin.utils.exception.GlobalException;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.errors.ErrorResponseException;
import io.minio.errors.InsufficientDataException;
import io.minio.errors.InternalException;
import io.minio.errors.InvalidResponseException;
import io.minio.errors.ServerException;
import io.minio.errors.XmlParserException;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 * @author xingxiaolin xing.xiaolin@foxmail.com
 * @Description 视频服务实现类
 * @create 2023/7/23
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class VideoServiceImpl implements VideoService {

    /**
     * 对象 key 的后缀部分：最多 8 位字母数字，且不含路径分隔符。
     * 完整 key 固定为 {@code videos/<uuid>.<后缀>}，绝不使用用户传入的原始文件名，
     * 以避免路径穿越与对象覆盖攻击。
     */
    private static final Pattern SAFE_SUFFIX = Pattern.compile("^[a-zA-Z0-9]{1,8}$");

    private final MinioClient minioClient;
    private final MessageProducer messageProducer;

    /**
     * 视频 bucket 名称，对应配置项 minio.video.bucket-name
     */
    @Value("${minio.video.bucket-name}")
    private String videoBucketName;

    /** MinIO 对外 endpoint，与 bucket、对象 key 拼成长期播放地址。 */
    @Value("${minio.endpoint}")
    private String minioEndpoint;

    /**
     * 视频本地目录配置值（相对用户 home 目录），对应配置项 app.video-file-dir
     */
    @Value("${app.video-file-dir}")
    private String videoFileDirName;

    /**
     * 视频本地目录绝对路径，初始化时由用户 home 目录与配置值拼接而成
     */
    private String videoFileDir;

    /**
     * 初始化视频本地目录绝对路径
     */
    @PostConstruct
    void initVideoFileDir() {
        this.videoFileDir = Path.of(System.getProperty("user.home"), videoFileDirName).toString();
    }

    /**
     * 上传整文件视频到对象存储，并投递转码任务。
     *
     * @param video     视频文件
     * @param videoType 视频类型
     * @param videoId   关联的影视 id
     * @return 稳定播放地址（公开对象 URL，不含预签名查询串）
     */
    @Override
    public String uploadVideoMinio(MultipartFile video, VideoTypeEnum videoType, Long videoId) {
        if (video.isEmpty()) {
            throw new GlobalException("视频内容为空");
        }
        requireVideoId(videoId);

        String resourceKey = newResourceKey();
        String objectKey = buildObjectKey(resourceKey, safeSuffix(video.getOriginalFilename(), videoType));

        try (BufferedInputStream inputStream = new BufferedInputStream(video.getInputStream())) {
            PutObjectArgs putObjectArgs = PutObjectArgs.builder()
                    .bucket(videoBucketName)
                    .object(objectKey)
                    .stream(inputStream, video.getSize(), -1)
                    .contentType(videoType.getContentType())
                    .build();
            minioClient.putObject(putObjectArgs);
        } catch (IOException | ErrorResponseException | InsufficientDataException | InternalException |
                 InvalidKeyException | InvalidResponseException | NoSuchAlgorithmException | ServerException |
                 XmlParserException e) {
            throw new GlobalException("上传视频失败：" + e.getMessage());
        }

        String videoUrl = playbackUrl(objectKey);
        publishTranscodeTask(videoUrl, resourceKey, videoId);
        return videoUrl;
    }

    /**
     * 视频分片上传
     * 实现步骤概述：
     *  1 定义目录md5，文件名chunkId，目标文件 videoFileDir/md5/chunkId
     *  2.1 查询是否已经存在，如果存在，直接返回下一个chunkId
     *  2.2 如果不存在，创建文件名，上传文件，返回下一个chunkId
     *
     * @param chunkVideo 分片文件
     * @param md5        视频标识
     * @param chunkId    分片序号
     * @param chunkMd5   分片摘要值
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
        Path dirPath = Path.of(videoFileDir, md5);
        File dirFile = dirPath.toFile();
        if (!dirFile.exists()) {
            return Boolean.TRUE;
        }
        try (Stream<Path> paths = Files.walk(dirPath)) {
            paths.sorted(Comparator.reverseOrder()).forEach(p -> {
                try {
                    Files.deleteIfExists(p);
                } catch (IOException e) {
                    log.warn("删除分片文件失败：{}", p, e);
                }
            });
        } catch (IOException e) {
            log.warn("清理分片目录失败：{}", dirPath, e);
            return Boolean.FALSE;
        }
        return Boolean.TRUE;
    }

    /**
     * 视频分片合并，并上传、投递转码任务。
     *
     * <p>实现步骤概述：
     * <ol>
     *   <li>合并分片（仅当目标文件不存在时才合并）；</li>
     *   <li>上传合并后的文件到对象存储；</li>
     *   <li>投递转码任务。</li>
     * </ol>
     *
     * <p>注意：历史上这三步被包在同一个 {@code if (!targetFile.exists())} 里，
     * 导致「合并文件已存在但上次没发消息」时消息永远不补发、视频永远不被转码。
     * 现在只有合并步骤受该判断保护。
     *
     * @param md5       视频标识（分片目录名）
     * @param videoType 视频类型
     * @param fileName  合并后的文件名（不含后缀）
     * @param videoId   关联的影视 id
     */
    @Override
    public void videoChunksMerge(String md5, VideoTypeEnum videoType, String fileName, Long videoId) {
        log.info("Start merge video chunks: {}, type: {}", md5, videoType.getCode());
        requireVideoId(videoId);

        Path dirPath = Path.of(videoFileDir, md5);
        File dirFile = dirPath.toFile();
        if (!dirFile.exists()) {
            throw new GlobalException("文件目录不存在 " + dirPath);
        }

        // 步骤 1：合并分片（只有这一步受"文件已存在"保护）
        String videoName = fileName + videoType.getSuffix();
        Path targetPath = Path.of(videoFileDir, videoName);
        File targetFile = targetPath.toFile();
        if (targetFile.exists()) {
            log.info("合并文件已存在，跳过合并步骤：{}", targetPath);
        } else {
            mergeChunks(dirFile, targetFile);
        }

        // 步骤 2：上传到对象存储
        String resourceKey = newResourceKey();
        String objectKey = buildObjectKey(resourceKey, safeSuffix(videoName, videoType));
        uploadFile2Minio(targetFile, objectKey, videoType);
        String videoUrl = playbackUrl(objectKey);

        // 步骤 3：投递转码任务（失败向上抛，由调用方重试，不再静默吞掉）
        publishTranscodeTask(videoUrl, resourceKey, videoId);
    }

    @Override
    public void videoChunksMerge(String md5, VideoTypeEnum videoType, Long videoId) {
        this.videoChunksMerge(md5, videoType, md5, videoId);
    }

    /**
     * 合并分片到目标文件。
     */
    private void mergeChunks(File dirFile, File targetFile) {
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
        log.info("合并完成：{}", targetFile.getName());
    }

    /**
     * 上传本地文件到对象存储。
     */
    private void uploadFile2Minio(File file, String objectKey, VideoTypeEnum videoType) {
        try (BufferedInputStream inputStream =
                     new BufferedInputStream(new FileInputStream(file))) {
            PutObjectArgs putObjectArgs = PutObjectArgs.builder()
                    .bucket(videoBucketName)
                    .object(objectKey)
                    .stream(inputStream, file.length(), -1)
                    .contentType(videoType.getContentType())
                    .build();
            minioClient.putObject(putObjectArgs);
            log.info("上传对象存储成功：{}", objectKey);
        } catch (IOException | ErrorResponseException | InsufficientDataException | InternalException |
                 InvalidKeyException | InvalidResponseException | NoSuchAlgorithmException | ServerException |
                 XmlParserException e) {
            throw new GlobalException("上传视频失败：" + e.getMessage());
        }
    }

    /**
     * 投递转码任务。发送失败直接抛出，由调用方感知并重试。
     *
     * <p>消息字段仍是 {@code sourceUrl}、{@code videoId}、{@code resourceKey}。
     * {@code sourceUrl} 为公开对象 URL，ffmpeg 直接拉取，回写时也不带过期签名。
     */
    private void publishTranscodeTask(String videoUrl, String resourceKey, Long videoId) {
        log.info("视频已上传，投递转码任务：videoId={}, resourceKey={}", videoId, resourceKey);
        messageProducer.sendTranscodeMessage(TranscodeMessage.builder()
                .sourceUrl(videoUrl)
                .videoId(videoId)
                .resourceKey(resourceKey)
                .build());
    }

    private void requireVideoId(Long videoId) {
        if (videoId == null) {
            throw new GlobalException("缺少 videoId，无法关联播放源");
        }
    }

    /**
     * 长期播放地址：{@code endpoint/bucket/key}，不含预签名查询串。
     */
    private String playbackUrl(String objectKey) {
        return publicObjectUrl(minioEndpoint, videoBucketName, objectKey);
    }

    static String publicObjectUrl(String endpoint, String bucket, String objectKey) {
        if (endpoint == null || endpoint.isBlank() || bucket == null || bucket.isBlank()
                || objectKey == null || objectKey.isBlank()) {
            throw new GlobalException("无法生成播放地址");
        }
        String base = endpoint.trim();
        while (base.endsWith("/")) {
            base = base.substring(0, base.length() - 1);
        }
        String key = objectKey.trim();
        while (key.startsWith("/")) {
            key = key.substring(1);
        }
        return base + "/" + bucket + "/" + key;
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

    /**
     * 生成资源标识：32 位十六进制（UUID 去横线）。
     *
     * <p>该标识同时作为：
     * <ul>
     *   <li>MinIO 对象 key 的主体部分；</li>
     *   <li>转码幂等键（has 侧按它判断产物是否已存在）；</li>
     *   <li>转码产物的目录名与播放列表文件名。</li>
     * </ul>
     */
    private String newResourceKey() {
        return UUID.randomUUID().toString().replace("-", "").toLowerCase(Locale.ROOT);
    }

    /**
     * 组装对象 key：{@code videos/<resourceKey><suffix>}。
     *
     * <p>图片与视频共用 bucket {@code raw}，固定前缀避免对象混放。
     * {@code resourceKey} 本身仍是 32 位 hex，转码幂等键不含此前缀。
     */
    private String buildObjectKey(String resourceKey, String suffix) {
        return "videos/" + resourceKey + suffix;
    }

    /**
     * 根据用户传入的文件名解析安全的对象后缀。
     *
     * <p>只保留扩展名中的字母数字部分，其余内容（路径、特殊字符、前导点）全部丢弃；
     * 后缀中不含 {@code /} 与 {@code ..}，无法跳出目标对象前缀。
     * 解析失败时回退到枚举中声明的后缀。
     */
    private String safeSuffix(String originalFilename, VideoTypeEnum videoType) {
        String suffix = "";
        if (originalFilename != null) {
            int dot = originalFilename.lastIndexOf('.');
            if (dot >= 0 && dot < originalFilename.length() - 1) {
                String ext = originalFilename.substring(dot + 1);
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
        // 调用方传入的扩展名不可用时，回退到枚举中声明的后缀
        if (suffix.isEmpty()) {
            suffix = videoType.getSuffix();
        }
        return suffix;
    }
}
