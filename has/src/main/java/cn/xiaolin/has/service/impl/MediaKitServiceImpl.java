package cn.xiaolin.has.service.impl;

import cn.xiaolin.has.config.AppConfigProperties;
import cn.xiaolin.has.config.MinioConfigProperties;
import cn.xiaolin.has.service.MediaKitService;
import cn.xiaolin.utils.exception.GlobalException;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.StatObjectArgs;
import io.minio.errors.*;
import io.minio.http.Method;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;


/**
 * Video transcoding service (mp4 -> HLS / DASH) using ffmpeg.
 *
 * @author xingxiaolin xing.xiaolin@foxmail.com
 * @create 2024/11/11
 */
@Slf4j
@Service
public class MediaKitServiceImpl implements MediaKitService {

    /**
     * Filename portion of a URL must look like an MD5 digest (32 hex chars, optional extension).
     * This prevents path-traversal payloads such as "../../etc/passwd" from escaping
     * the configured working directory.
     */
    private static final Pattern SAFE_FILENAME = Pattern.compile("^[a-fA-F0-9]{32}$");

    /**
     * Only http and https URLs are accepted as input sources. This blocks file://, gopher://
     * and other schemes that could lead to SSRF or local file disclosure via ffmpeg.
     */
    private static final String[] ALLOWED_SCHEMES = {"http", "https"};

    /** ffmpeg 结束后保留的 stderr 行数（仅用于失败时定位问题） */
    private static final int STDERR_TAIL_LINES = 20;

    /** HLS 与 DASH 共用 bucket transcoded，用对象前缀分开，避免同名分片互相覆盖。 */
    private static final String HLS_KEY_PREFIX = "hls/";
    private static final String DASH_KEY_PREFIX = "dash/";

    private final String hlsFileDir;
    private final String hlsBucketName;
    private final String dashFileDir;
    private final String dashBucketName;
    private final MinioClient minioClient;

    /**
     * ffmpeg 单次转码的最长执行时间。超过后强制结束进程，
     * 避免消费线程被一个挂死的网络请求永久占用。
     */
    private final long transcodeTimeoutMinutes;

    public MediaKitServiceImpl(AppConfigProperties appConfigProperties,
                               MinioConfigProperties minioConfigProperties,
                               MinioClient minioClient,
                               @Value("${app.transcode-timeout-minutes:120}") long transcodeTimeoutMinutes) {
        this.hlsFileDir = appConfigProperties.getHlsFileDir();
        this.hlsBucketName = minioConfigProperties.getHls().getBucketName();
        this.dashFileDir = appConfigProperties.getDashFileDir();
        this.dashBucketName = minioConfigProperties.getDash().getBucketName();
        this.minioClient = minioClient;
        this.transcodeTimeoutMinutes = transcodeTimeoutMinutes;
    }

    @Override
    public String media2Hls(String videoUrl, String resourceKey) {
        log.info("开始转码，mp4 -> hls");
        validateVideoUrl(videoUrl);
        validateFilename(resourceKey);

        String playlistName = resourceKey + ".m3u8";
        String objectKey = HLS_KEY_PREFIX + resourceKey + "/" + playlistName;

        // 幂等：产物已存在则直接复用，不再重复转码（避免重复投递造成重复计算与覆盖）
        if (objectExists(hlsBucketName, objectKey)) {
            log.info("HLS 产物已存在，跳过转码：{}", objectKey);
            return presignedUrl(hlsBucketName, objectKey);
        }

        Path targetDirPath = prepareWorkDir(hlsFileDir, resourceKey);
        String hlsName = targetDirPath.resolve(playlistName).toString();

        int exitCode = runFfmpeg(List.of(
                "-i", videoUrl,
                "-c:v", "libx264",
                "-c:a", "aac",
                "-hls_time", "10",
                "-hls_list_size", "0",
                "-f", "hls",
                hlsName
        ), "HLS");

        if (exitCode != 0) {
            cleanupWorkDir(targetDirPath);
            throw new GlobalException("视频转 HLS 失败，退出码: " + exitCode);
        }

        try {
            uploadFolder(targetDirPath, hlsBucketName, HLS_KEY_PREFIX);
        } finally {
            // 临时目录转码后即清理，避免磁盘被转码产物撑满
            cleanupWorkDir(targetDirPath);
        }

        String m3u8Url = presignedUrl(hlsBucketName, objectKey);
        log.info("HLS 访问 URL：{}", m3u8Url);
        return m3u8Url;
    }

    @Override
    public String media2Dash(String videoUrl, String resourceKey) {
        log.info("开始转码，mp4 -> dash");
        validateVideoUrl(videoUrl);
        validateFilename(resourceKey);

        String playlistName = resourceKey + ".mpd";
        String objectKey = DASH_KEY_PREFIX + resourceKey + "/" + playlistName;

        if (objectExists(dashBucketName, objectKey)) {
            log.info("DASH 产物已存在，跳过转码：{}", objectKey);
            return presignedUrl(dashBucketName, objectKey);
        }

        Path targetDirPath = prepareWorkDir(dashFileDir, resourceKey);
        String dashName = targetDirPath.resolve(playlistName).toString();

        // 修正：原实现重复三次 "-map 0:v:0 -map 0:a:0"，但只配了一组码率参数，
        // 实际只输出一路流，第二、三组参数被静默忽略。这里输出单路 1080p。
        int exitCode = runFfmpeg(List.of(
                "-hide_banner", "-y",
                "-i", videoUrl,
                "-map", "0:v:0",
                "-map", "0:a:0",
                "-b:v", "1750k",
                "-s:v", "1920x1080",
                "-use_timeline", "1",
                "-use_template", "1",
                "-adaptation_sets", "id=0,streams=v id=1, streams=a",
                "-f", "dash",
                dashName
        ), "DASH");

        if (exitCode != 0) {
            cleanupWorkDir(targetDirPath);
            throw new GlobalException("视频转 DASH 失败，退出码: " + exitCode);
        }

        try {
            uploadFolder(targetDirPath, dashBucketName, DASH_KEY_PREFIX);
        } finally {
            cleanupWorkDir(targetDirPath);
        }

        String mpdUrl = presignedUrl(dashBucketName, objectKey);
        log.info("DASH 访问 URL：{}", mpdUrl);
        return mpdUrl;
    }

    /**
     * 上传文件夹下的内容到 minio
     *
     * @param dirPath    文件夹路径
     * @param bucketName 目标 bucket
     */
    @Override
    public void uploadFolder2Minio(Path dirPath, String bucketName) {
        uploadFolder(dirPath, bucketName, "");
    }

    /**
     * 上传文件夹。{@code keyPrefix} 拼在对象 key 前面，用于在同一 bucket 内区分 HLS / DASH。
     */
    private void uploadFolder(Path dirPath, String bucketName, String keyPrefix) {
        File dirFile = dirPath.toFile();
        String folderName = dirFile.getName();
        File[] files = dirFile.listFiles();
        if (Objects.isNull(files) || files.length == 0) {
            throw new GlobalException("文件夹为空");
        }
        String prefix = keyPrefix == null ? "" : keyPrefix;
        Arrays.stream(files).sorted(Comparator.comparing(File::getName)).forEach(file -> {
            String contentType = "application/octet-stream";
            String fileName = file.getName();
            try (BufferedInputStream inputStream = new BufferedInputStream(new FileInputStream(file))) {
                PutObjectArgs putObjectArgs = PutObjectArgs.builder()
                        .bucket(bucketName)
                        .object(prefix + folderName + '/' + fileName)
                        .stream(inputStream, file.length(), -1)
                        .contentType(contentType)
                        .build();
                minioClient.putObject(putObjectArgs);
                log.info("上传成功：{}", file.getName());
            } catch (IOException | ErrorResponseException | InsufficientDataException | InternalException |
                     InvalidKeyException | InvalidResponseException | NoSuchAlgorithmException | ServerException |
                     XmlParserException e) {
                throw new GlobalException("上传失败：" + e.getMessage());
            }
        });
    }

    /**
     * 执行 ffmpeg 并等待结束。
     *
     * <p>关键点：
     * <ul>
     *   <li>带超时：{@code waitFor(timeout, unit)}，超时后强制结束进程；</li>
     *   <li>stderr 不再全量打 INFO（大文件转码会刷出海量日志），只在失败时输出尾部若干行；</li>
     *   <li>中断时恢复线程中断标志，避免取消信号丢失。</li>
     * </ul>
     *
     * @return ffmpeg 退出码
     */
    private int runFfmpeg(List<String> command, String format) {
        ProcessBuilder processBuilder = new ProcessBuilder(command);
        processBuilder.redirectErrorStream(true);
        Process process = null;
        try {
            process = processBuilder.start();

            // 读取输出，避免管道写满导致 ffmpeg 阻塞
            StringBuilder tail = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (tail.length() > 0) {
                        tail.append('\n');
                    }
                    tail.append(line);
                    if (tail.length() > 8192) {
                        // 只保留尾部，防止无限增长
                        tail.delete(0, tail.length() - 4096);
                    }
                }
            }

            boolean finished = process.waitFor(transcodeTimeoutMinutes, TimeUnit.MINUTES);
            if (!finished) {
                process.destroyForcibly();
                log.error("{} 转码超时（超过 {} 分钟），已强制结束进程", format, transcodeTimeoutMinutes);
                return -1;
            }
            int exitCode = process.exitValue();
            if (exitCode != 0) {
                log.error("{} 转码失败，退出码: {}，ffmpeg 输出尾部:\n{}", format, exitCode, tail);
            } else {
                log.info("{} 转码成功", format);
            }
            return exitCode;
        } catch (InterruptedException e) {
            // 恢复中断标志，否则上层无法感知取消
            Thread.currentThread().interrupt();
            if (process != null) {
                process.destroyForcibly();
            }
            throw new GlobalException("转码被中断");
        } catch (IOException e) {
            throw new GlobalException("执行 ffmpeg 失败：" + e.getMessage());
        }
    }

    /**
     * 准备转码工作目录：{@code <user.home>/<fileDir>/<resourceKey>}
     */
    private Path prepareWorkDir(String fileDir, String resourceKey) {
        String homeDir = System.getProperty("user.home");
        Path dirPath = Path.of(homeDir, fileDir, resourceKey);
        File dirFile = dirPath.toFile();
        if (!dirFile.exists() && !dirFile.mkdirs()) {
            throw new GlobalException("创建文件夹失败：" + dirPath);
        }
        return dirPath;
    }

    /**
     * 清理转码临时目录。清理失败只告警，不影响主流程。
     */
    private void cleanupWorkDir(Path dirPath) {
        try (var paths = Files.walk(dirPath)) {
            paths.sorted(Comparator.reverseOrder()).forEach(p -> {
                try {
                    Files.deleteIfExists(p);
                } catch (IOException e) {
                    log.warn("删除转码临时文件失败：{}", p, e);
                }
            });
        } catch (IOException e) {
            log.warn("清理转码临时目录失败：{}", dirPath, e);
        }
    }

    /**
     * 判断对象是否已存在于 MinIO，用于转码幂等。
     */
    private boolean objectExists(String bucketName, String objectKey) {
        try {
            minioClient.statObject(StatObjectArgs.builder()
                    .bucket(bucketName)
                    .object(objectKey)
                    .build());
            return true;
        } catch (ErrorResponseException e) {
            // NoSuchKey / NoSuchObject 视为不存在
            return false;
        } catch (InsufficientDataException | InternalException | InvalidKeyException | InvalidResponseException |
                 IOException | NoSuchAlgorithmException | ServerException | XmlParserException e) {
            log.warn("查询 MinIO 对象状态失败：{}/{}", bucketName, objectKey, e);
            return false;
        }
    }

    /**
     * 生成对象的预签名访问 URL。
     */
    private String presignedUrl(String bucketName, String objectKey) {
        GetPresignedObjectUrlArgs args = GetPresignedObjectUrlArgs.builder()
                .bucket(bucketName)
                .object(objectKey)
                .method(Method.GET)
                .build();
        try {
            return minioClient.getPresignedObjectUrl(args);
        } catch (ErrorResponseException | InsufficientDataException | InternalException | InvalidKeyException |
                 InvalidResponseException | IOException | NoSuchAlgorithmException | XmlParserException |
                 ServerException e) {
            throw new GlobalException(e.getMessage());
        }
    }

    /**
     * Ensure the input URL uses an allowed scheme. This restricts ffmpeg from
     * accessing local files (file://) or non-HTTP schemes that could be abused
     * for SSRF / local file disclosure.
     */
    private void validateVideoUrl(String videoUrl) {
        if (videoUrl == null || videoUrl.isBlank()) {
            throw new GlobalException("视频 URL 不能为空");
        }
        try {
            URL url = new URL(videoUrl);
            String scheme = url.getProtocol();
            if (scheme == null) {
                throw new GlobalException("视频 URL 协议不合法");
            }
            boolean ok = Arrays.stream(ALLOWED_SCHEMES)
                    .anyMatch(s -> s.equalsIgnoreCase(scheme));
            if (!ok) {
                throw new GlobalException("视频 URL 协议不被允许");
            }
        } catch (MalformedURLException e) {
            throw new GlobalException("视频 URL 格式不合法");
        }
    }

    /**
     * Ensure the filename portion of the URL is a 32-char hex string, blocking
     * path-traversal sequences and other shell-meaningful characters from
     * reaching the filesystem or ffmpeg arguments.
     */
    private void validateFilename(String filename) {
        if (filename == null || !SAFE_FILENAME.matcher(filename.toLowerCase(Locale.ROOT)).matches()) {
            throw new GlobalException("视频文件名不合法");
        }
    }
}
