package cn.xiaolin.multimedia.service;

import java.io.File;
import java.nio.file.Path;

/**
 * @author xingxiaolin xing.xiaolin@foxmail.com
 * @Description 文件上传到阿里云OSS
 * @create 2023/7/23
 */
public interface AliOssService {

    String uploadFile2Oss(String bucketName, String inputFilePath);
    String uploadFile2Oss(String bucketName, Path inputPath);
    String uploadFile2Oss(String bucketName, File file);
    String uploadFile2Oss(String inputFilePath);
    String uploadFile2Oss(Path inputPath);
    String uploadFile2Oss(File file);


    String uploadDirectory2Oss(String bucketName, String inputFileDir);
    String uploadDirectory2Oss(String bucketName, Path inputDir);
    String uploadDirectory2Oss(String bucketName, File file);
    String uploadDirectory2Oss(String inputFileDir);
    String uploadDirectory2Oss(Path inputDir);
    String uploadDirectory2Oss(File dir);

    String uploadDirectory2Oss(File dir, String ossFileDirectory);

}
