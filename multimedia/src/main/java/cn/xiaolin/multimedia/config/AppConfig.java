package cn.xiaolin.multimedia.config;

import cn.xiaolin.message.msg.MediaUploadMsg;
import net.bramp.ffmpeg.FFmpeg;
import net.bramp.ffmpeg.FFprobe;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author xingxiaolin xing.xiaolin@foxmail.com
 * @Description 媒体处理配置
 * @create 2023/7/29
 */
@Configuration
public class AppConfig {

    @Bean
    public FFmpeg ffmpeg() throws IOException {
        return new FFmpeg();
    }

    @Bean
    public FFprobe ffprobe() throws IOException {
        return new FFprobe();
    }

    /**
     * 线程安全的全局共享Map池
     * @return 用来存储视频上传消息的线程完全Map
     */
    @Bean
    public ConcurrentMap<Long, MediaUploadMsg> mediaConcurrentHashMap() {
        return new ConcurrentHashMap<>();
    }

    @Bean
    public ConcurrentMap<String, Long> resourceIdMap() {
        return new ConcurrentHashMap<>();
    }
}
