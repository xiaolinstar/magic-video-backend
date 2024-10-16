package cn.xiaolin.multimedia.service;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;


/**
 * @author xingxiaolin xing.xiaolin@foxmail.com
 * @Description 图像上传下载服务
 * @create 2023/7/23
 */
public interface ImageService {

    /**
     * 图像上传
     * @param file 图像
     * @return 图像资源ID
     */
    String imageUpload(MultipartFile file);

    /**
     * 字符串加密上传文件
     * @param imageBase64 图像的64位加密
     * @return 图像资源ID
     */
    String imageUpload(String imageBase64);

}
