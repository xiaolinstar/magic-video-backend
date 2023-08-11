package cn.xiaolin.multimedia.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author xingxiaolin xlxing@bupt.edu.cn
 * @Description 线程池配置
 * @create 2023/7/23
 */
@Configuration
public class ThreadPoolConfig {
    private static final int CORE_SIZE = 2;
    private static final int MAX_POOL_SIZE = 3;

    private static final int QUEUE_CAPACITY = 10;

    /**
     * 视频转码线程池
     * @return Spring线程池
     */
    @Bean
    public ThreadPoolTaskExecutor mediaExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(CORE_SIZE);
        executor.setMaxPoolSize(MAX_POOL_SIZE);
        executor.setQueueCapacity(QUEUE_CAPACITY);
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.AbortPolicy());
        executor.setThreadNamePrefix("MediaThread-");
        executor.initialize();
        return executor;
    }
}
