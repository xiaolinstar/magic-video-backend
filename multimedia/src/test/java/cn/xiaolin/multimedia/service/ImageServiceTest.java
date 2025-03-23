package cn.xiaolin.multimedia.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author xingxiaolin xing.xiaolin@foxmail.com
 * @Description 图像服务测试类
 * @create 2025/3/11
 */
@SpringBootTest
@ActiveProfiles("dev")
class ImageServiceTest {

    @Autowired
    private ImageService imageService;

    @Test
    public void testImageUpload() throws IOException {
        String imageUrl = "/Users/xlxing/Downloads/crazy-max.jpg";
        Path imagePath = Path.of(imageUrl);
        byte[] imageBytes = Files.readAllBytes(imagePath);
        MultipartFile multipartFile = new MockMultipartFile(
                "疯狂麦克斯",
                "crazy-max.jpg",
                "image/jpeg",
                imageBytes
        );

        String url = imageService.imageUpload(multipartFile);
        assertNotNull(url);
        System.out.printf("疯狂麦克斯: %s\n", url);
    }
}