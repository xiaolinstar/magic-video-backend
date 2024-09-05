package cn.xiaolin.multimedia.service;

import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.tuple.Pair;
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
    Long imageUpload(MultipartFile file);

    /**
     * 字符串加密上传文件
     * @param imageBase64 图像的64位加密
     * @return 图像资源ID
     */
    Long imageUpload(String imageBase64);

    /**
     * ServletOutputStream流式加载
     * 返回值必须为void
     * @param response HttpServletResponse
     * @param filePath 文件路径
     */
    void imageLoad(HttpServletResponse response, String filePath);


    /**
     * 图像base64编码
     * @param filePath 文件路径
     * @return base64字符串图像
     */
    String imageLoad(String filePath);
}
