package cn.xiaolin.multimedia.controller;

import cn.xiaolin.utils.resp.Result;
import cn.xiaolin.multimedia.service.ImageService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.NotImplementedException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

/**
 * @author xingxiaolin xlxing@bupt.edu.cn
 * @Description 文件处理控制器
 * @create 2023/7/23
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class ImageController {
    private final ImageService imageService;

    @PostMapping("/image")
    public Result<Map<String, Long>> one(MultipartFile file) {
        throw new NotImplementedException();
    }

    @GetMapping("/image/{id}")
    public void getOne(HttpServletResponse response, @PathVariable Long id) {
         throw new NotImplementedException();
    }

}
