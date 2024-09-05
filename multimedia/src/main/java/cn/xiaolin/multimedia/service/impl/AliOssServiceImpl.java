package cn.xiaolin.multimedia.service.impl;

import cn.xiaolin.utils.exception.GlobalException;
import cn.xiaolin.multimedia.service.AliOssService;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.model.PutObjectRequest;
import com.aliyun.oss.model.PutObjectResult;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Objects;

/**
 * @author xingxiaolin xing.xiaolin@foxmail.com
 * @Description
 * @create 2023/7/23
 */
@Service
@RequiredArgsConstructor
public class AliOssServiceImpl implements AliOssService {
    private final OSS ossClient;
    @Value("${file.video.bucket-name}")
    private String bucketName;

    @Override
    public String uploadFile2Oss(String bucketName, String inputFilePath) {
        try {
            PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, inputFilePath, new File(inputFilePath));
            PutObjectResult putObjectResult = ossClient.putObject(putObjectRequest);
            return putObjectResult.getETag();
        } catch (OSSException e) {
            throw new GlobalException("上传文件失败，阿里云OSS错误：" + e.getMessage());
        } catch (Exception e) {
            throw new GlobalException("上传文件失败，服务器错误：" + e.getMessage());
        }
    }

    @Override
    public String uploadFile2Oss(String bucketName, Path inputPath) {
        return null;
    }

    @Override
    public String uploadFile2Oss(String bucketName, File file) {
        return null;
    }

    @Override
    public String uploadFile2Oss(String inputFilePath) {
        return null;
    }

    @Override
    public String uploadFile2Oss(Path inputPath) {
        return null;
    }

    @Override
    public String uploadFile2Oss(File file) {
        return null;
    }

    @Override
    public String uploadDirectory2Oss(String bucketName, String inputFileDir) {
        return null;
    }

    @Override
    public String uploadDirectory2Oss(String bucketName, Path inputDir) {
        return null;
    }

    @Override
    public String uploadDirectory2Oss(String bucketName, File file) {
        return null;
    }

    @Override
    public String uploadDirectory2Oss(String inputFileDir) {
        File dir = new File(inputFileDir);
        return uploadDirectory2Oss(dir);
    }

    @Override
    public String uploadDirectory2Oss(Path inputDir) {
        return null;
    }

    @Override
    public String uploadDirectory2Oss(File dir) {
        if (!dir.exists() || !dir.isDirectory()) {
            throw new GlobalException("不存在的文件夹：" + dir.getPath());
        }
        try {
            File[] files = dir.listFiles();
            if (Objects.isNull(files)) {
                return "success";
            }
            Arrays.stream(files)
                    .filter(file -> !file.isDirectory())
                    .forEach(file -> {
                        PutObjectRequest request = new PutObjectRequest(bucketName, file.getPath(), file);
                        ossClient.putObject(request);
                    });
            return "MySuccess";
        } catch (OSSException e) {
            throw new GlobalException("上传文件夹失败，阿里云OSS错误：" + e.getMessage());
        } catch (Exception e) {
            throw new GlobalException("服务器错误：" + e.getMessage());
        }
    }

    @Override
    public String uploadDirectory2Oss(File dir, String ossFileDirectory) {
        if (!dir.exists() || !dir.isDirectory()) {
            throw new GlobalException("不存在的文件夹：" + dir.getPath());
        }
        try {
            File[] files = dir.listFiles();
            if (Objects.isNull(files)) {
                return "success";
            }
            Arrays.stream(files)
                    .filter(file -> !file.isDirectory())
                    .forEach(file -> {
                        String targetFilePath = Paths.get(ossFileDirectory, file.getName()).toString();
                        PutObjectRequest request = new PutObjectRequest(bucketName, targetFilePath, file);
                        ossClient.putObject(request);
                    });
            return "MySuccess";
        } catch (OSSException e) {
            throw new GlobalException("上传文件夹失败，阿里云OSS错误：" + e.getMessage());
        } catch (Exception e) {
            throw new GlobalException("服务器错误：" + e.getMessage());
        }
    }
}
