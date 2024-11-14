package cn.xiaolin.has.config;

import net.bramp.ffmpeg.FFmpeg;
import net.bramp.ffmpeg.FFprobe;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

/**
 * @author xingxiaolin xing.xiaolin@foxmail.com
 * @Description has微服务第三方Bean
 * @create 2024/11/11
 */
@Configuration
public class AppConfig {

    @Bean
    public FFmpeg ffmpeg() throws IOException {
        return new FFmpeg();
    }

    @Bean
    public FFprobe fFprobe() throws IOException {
        return new FFprobe();
    }


}
