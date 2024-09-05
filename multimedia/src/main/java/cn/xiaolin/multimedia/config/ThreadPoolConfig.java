package cn.xiaolin.multimedia.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author xingxiaolin xing.xiaolin@foxmail.com
 * @Description 线程池配置
 * @create 2023/7/23
 */
@Configuration
@EnableConfigurationProperties(AppConfigProperties.class)
@RequiredArgsConstructor
public class ThreadPoolConfig {

    private final AppConfigProperties appConfigProperties;

    /**
     * 视频转码线程池
     * @return Spring线程池
     */
    @Bean
    public ThreadPoolTaskExecutor mediaExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(appConfigProperties.getCoreSize());
        executor.setMaxPoolSize(appConfigProperties.getMaxPoolSize());
        executor.setQueueCapacity(appConfigProperties.getQueueCapacity());
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.AbortPolicy());
        executor.setThreadNamePrefix(appConfigProperties.getThreadPrefix());
        executor.initialize();
        return executor;
    }
}
