package cn.xiaolin.multimedia.config;

import cn.xiaolin.utils.exception.GlobalException;
import cn.xiaolin.multimedia.enums.FileTypeEnum;
import cn.xiaolin.multimedia.utils.FileUtil;
import cn.xiaolin.multimedia.utils.FileUtilImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.nio.file.FileSystemException;

/**
 * @author xingxiaolin xlxing@bupt.edu.cn
 * @Description 文件传输相关配置
 * @create 2023/7/29
 */

@Configuration
public class FileConfig {

    @Value("${file.root.dir}")
    private String rootDir;

    @Value("${file.image.dir}")
    private String imageDir;

    @Value("${file.video.dir}")
    private String videoDir;

    @Bean
    public FileUtil imageUtil() {
        try {
            return new FileUtilImpl(rootDir, imageDir);
        } catch (FileSystemException e) {
            throw new GlobalException(e.getMessage());
        }
    }

    @Bean
    public FileUtil videoUtil() {
        try {
            return new FileUtilImpl(rootDir, videoDir);
        } catch (FileSystemException e) {
            throw new GlobalException(e.getMessage());
        }
    }
}
