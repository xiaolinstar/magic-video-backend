package cn.xiaolin.has.service.impl;

import cn.xiaolin.has.config.AppConfigProperties;
import cn.xiaolin.has.config.MinioConfigProperties;
import cn.xiaolin.has.service.MediaKitService;
import cn.xiaolin.utils.exception.GlobalException;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.errors.*;
import io.minio.http.Method;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Locale;
import java.util.Objects;
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

    private final String hlsFileDir;
    private final String hlsBucketName;
    private final String dashFileDir;
    private final String dashBucketName;
    private final MinioClient minioClient;
    public MediaKitServiceImpl(AppConfigProperties appConfigProperties,
                               MinioConfigProperties minioConfigProperties,
                               MinioClient minioClient) {
        this.hlsFileDir = appConfigProperties.getHlsFileDir();
        this.hlsBucketName = minioConfigProperties.getHls().getBucketName();
        this.dashFileDir = appConfigProperties.getDashFileDir();
        this.dashBucketName = minioConfigProperties.getDash().getBucketName();
        this.minioClient = minioClient;
    }

    /**
     * video 转 hls
     * @param videoUrl mp4的访问url
     */
    @Override
    public void media2Hls(String videoUrl) {
        log.info("开始转码，mp4 -> hls");
        String md5 = getUrlFilename(videoUrl);
        validateVideoUrl(videoUrl);
        validateFilename(md5);

        log.info("videoUrl: {}", videoUrl);
        log.info("md5: {}", md5);

        String homeDir = System.getProperty("user.home");
        Path hlsDirPath = Path.of(homeDir, hlsFileDir);

        // 宿主机上的临时文件： /home/xingxiaolin/xx-md5
        Path targetDirPath = Path.of(hlsDirPath.toString(), md5);
        File targetDirFile = targetDirPath.toFile();
        if (!targetDirFile.exists()) {
            if (!targetDirFile.mkdirs()) {
                log.error("创建文件夹失败：{}", targetDirPath);
                throw new GlobalException("创建文件夹失败：" + targetDirPath);
            } else {
                log.info("创建文件夹成功：{}", targetDirPath);
            }
        } else {
            log.info("文件夹已存在：{}", targetDirPath);
        }

        String m3u8Name = md5 + ".m3u8";
        String hlsName = Path.of(targetDirPath.toString(), m3u8Name).toString();
        // 创建 ffmpeg 命令，将视频转成 HLS 格式
        ProcessBuilder processBuilder = new ProcessBuilder(
                "ffmpeg",
                "-i", videoUrl,
                "-c:v", "libx264",
                "-c:a", "aac",
                "-hls_time", "10",
                "-hls_list_size", "0",
                "-f", "hls",
                hlsName
        );

        try {
            // 启动进程
            Process process = processBuilder.start();

            // 获取进程的输出流
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getErrorStream()))){
                String line;
                while ((line = reader.readLine()) != null) {
                    log.info(line);
                }

                // 等待进程执行完毕
                int exitCode = process.waitFor();
                if (exitCode == 0) {
                    log.info("视频转 HLS 成功");
                    // 视频上传到 MinIO
                    uploadFolder2Minio(targetDirPath, hlsBucketName);
                } else {
                    log.error("视频转 HLS 失败，退出码: {}", exitCode);
                }
            } catch (IOException e) {
                log.error("读取视频转 HLS 失败");
            }
        } catch (InterruptedException | IOException e) {
            throw new RuntimeException(e);
        }

        GetPresignedObjectUrlArgs args = GetPresignedObjectUrlArgs.builder()
                .bucket(hlsBucketName)
                .object(m3u8Name)
                .method(Method.GET)
                .build();
        try {
            String m3u8Url = minioClient.getPresignedObjectUrl(args);
            log.info("HLS 访问 URL：{}", m3u8Url);
        } catch (ErrorResponseException | InsufficientDataException | InternalException | InvalidKeyException |
                 InvalidResponseException | IOException | NoSuchAlgorithmException | XmlParserException |
                 ServerException e) {
            throw new GlobalException(e.getMessage());
        }
    }

    /**
     * 上传文件夹下的内容到 minio
     * @param dirPath 文件夹路径
     */
    @Override
    public void uploadFolder2Minio(Path dirPath, String bucketName) {
        File dirFile = dirPath.toFile();
        String folderName = dirFile.getName();
        File[] files = dirFile.listFiles();
        if (Objects.isNull(files)) {
            throw new GlobalException("文件夹为空");
        }
        Arrays.stream(files).forEach(file -> {
            String contentType = "application/octet-stream";
            String fileName = file.getName();
            try (BufferedInputStream inputStream = new BufferedInputStream(new FileInputStream(file))) {
                PutObjectArgs putObjectArgs = PutObjectArgs.builder()
                        .bucket(bucketName)
                        .object(folderName + '/' + fileName)
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


    private String getUrlFilename(String urlStr) {
        try {
            URL url = new URL(urlStr);
            String path = url.getPath();
            String[] parts = path.split("/");
            String md5mp4 = parts[parts.length - 1];
            return md5mp4.split("\\.")[0];
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
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

    @Override
    public void media2Dash(String videoUrl) {
        log.info("开始转码，mp4 -> dash");
        validateVideoUrl(videoUrl);
        String videoName = getUrlFilename(videoUrl);
        validateFilename(videoName);

        log.info("videoUrl: {}", videoUrl);
        log.info("videoName: {}", videoName);

        String homeDir = System.getProperty("user.home");
        Path dashDirPath = Path.of(homeDir, dashFileDir);

        // 宿主机上的临时文件： /home/xingxiaolin/xx-videoName
        Path targetDirPath = Path.of(dashDirPath.toString(), videoName);
        File targetDirFile = targetDirPath.toFile();
        if (!targetDirFile.exists()) {
            if (!targetDirFile.mkdirs()) {
                log.error("创建文件夹失败：{}", targetDirPath);
                throw new GlobalException("创建文件夹失败：" + targetDirPath);
            } else {
                log.info("创建文件夹成功：{}", targetDirPath);
            }
        } else {
            log.info("文件夹已存在：{}", targetDirPath);
        }

        String mpdName = videoName + ".mpd";
        String dashName = Path.of(targetDirPath.toString(), mpdName).toString();
        // 创建 ffmpeg 命令，将视频转成 HLS 格式
        ProcessBuilder processBuilder = new ProcessBuilder(
                "ffmpeg",
                "-hide_banner", "-y",
                "-i", videoUrl, // 输入文件
                "-map", "0:v:0", "-map", "0:a:0", // 选择视频流，选择音频流
                "-map", "0:v:0", "-map", "0:a:0", // 选择视频流，选择音频流
                "-map", "0:v:0", "-map", "0:a:0", // 选择视频流，选择音频流
                "-b:v:0", "1750k", "-s:v:0", "720x480",
                "-b:v:1", "3000k", "-s:v:1", "1280x720",
                "-b:v:2", "4900k", "-s:v:2", "1920x1080",
                "-use_timeline", "1",
                "-use_template", "1",
                "-adaptation_sets", "id=0,streams=v id=1, streams=a",
                "-f", "dash",
                dashName // 输出 MPD 文件
        );
        try {
            // 启动进程
            Process process = processBuilder.start();

            // 获取进程的输出流
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getErrorStream()))){
                String line;
                while ((line = reader.readLine()) != null) {
                    log.info(line);
                }

                // 等待进程执行完毕
                int exitCode = process.waitFor();
                if (exitCode == 0) {
                    log.info("视频转 DASH 成功");
                    // 视频上传到 MinIO
                    uploadFolder2Minio(targetDirPath, dashBucketName);
                } else {
                    log.error("视频转 DASH 失败，退出码: {}", exitCode);
                }
            } catch (IOException e) {
                log.error("读取视频转 DASH 失败");
                throw new GlobalException(e.getMessage());
            }
        } catch (InterruptedException | IOException e) {
            throw new RuntimeException(e);
        }

        GetPresignedObjectUrlArgs args = GetPresignedObjectUrlArgs.builder()
                .bucket(dashBucketName)
                .object(mpdName)
                .method(Method.GET)
                .build();
        try {
            String mpdUrl = minioClient.getPresignedObjectUrl(args);
            log.info("DASH 访问URL：{}", mpdUrl);
        } catch (ErrorResponseException | InsufficientDataException | InternalException | InvalidKeyException |
                 InvalidResponseException | IOException | NoSuchAlgorithmException | XmlParserException |
                 ServerException e) {
            throw new GlobalException(e.getMessage());
        }
    }
}
