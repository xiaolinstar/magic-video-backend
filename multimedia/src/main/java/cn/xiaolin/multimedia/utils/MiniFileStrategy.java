package cn.xiaolin.multimedia.utils;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * 文件上传到服务器，返回文件路径
 * @author xingxiaolin xing.xiaolin@foxmail.com
 * @Description 常规文件或小文件上传策略
 * @create 2023/7/9
 */
public interface MiniFileStrategy {

    /**
     * 文件上传
     * @param file 图像
     * @return 可寻址的文件路径
     */
    String fileUpload(MultipartFile file) throws IOException;

    /**
     * 字符串加密上传文件
     * @param fileBase64 图像的64位加密
     * @return 可寻址的文件路径
     */
    String fileUpload(String fileBase64);

    /**
     * ServletOutputStream流式加载
     * 返回值必须为void
     * @param response HttpServletResponse
     * @param filePath 可寻址的文件路径
     */
    void fileLoad(HttpServletResponse response, String filePath) throws IOException;


    /**
     * 文件base64编码
     * @param filePath 可寻址的文件路径
     * @return base64字符串文件
     */
    String fileLoad(String filePath);
}
