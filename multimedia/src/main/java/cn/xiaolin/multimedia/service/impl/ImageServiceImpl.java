package cn.xiaolin.multimedia.service.impl;

import cn.xiaolin.utils.exception.GlobalException;
import cn.xiaolin.multimedia.utils.FileUtil;
import cn.xiaolin.multimedia.service.ImageService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.NotImplementedException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * @author xingxiaolin xing.xiaolin@foxmail.com
 * @Description
 * @create 2023/7/23
 */
@Service
@RequiredArgsConstructor
public class ImageServiceImpl implements ImageService {

    private final FileUtil imageUtil;


    /**
     * 图像上传
     *
     * @param file 图像
     * @return 图像资源ID
     */
    @Override
    public Long imageUpload(MultipartFile file){
        try {
            String filePath = imageUtil.fileUpload(file);
            return null;
        } catch (IOException e) {
            throw new GlobalException(e.getMessage());
        }
    }

    /**
     * 字符串加密上传文件
     *
     * @param imageBase64 图像的64位加密
     * @return 图像资源ID
     */
    @Override
    public Long imageUpload(String imageBase64) {
        throw new NotImplementedException();
    }

    /**
     * ServletOutputStream流式加载
     * 返回值必须为void
     *
     * @param response HttpServletResponse
     * @param filePath 文件路径
     */
    @Override
    public void imageLoad(HttpServletResponse response, String filePath){
        try {
            imageUtil.fileLoad(response, filePath);
        } catch (IOException e) {
            throw new GlobalException(e.getMessage());
        }
    }

    /**
     * 图像base64编码
     *
     * @param filePath 图像资源ID
     * @return base64字符串图像
     */
    @Override
    public String imageLoad(String filePath) {
        return imageUtil.fileLoad(filePath);
    }
}
