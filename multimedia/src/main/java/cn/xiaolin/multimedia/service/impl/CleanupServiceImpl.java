package cn.xiaolin.multimedia.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.context.SmartLifecycle;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * @author xingxiaolin xing.xiaolin@foxmail.com
 * @Description 程序退出时，关闭资源
 * @create 2023/7/23
 */
@Service
@RequiredArgsConstructor
public class CleanupServiceImpl implements SmartLifecycle {

    private volatile boolean running = false;

    private final ThreadPoolTaskExecutor mediaExecutor;

    private void cleanup() {
        if (Objects.nonNull(mediaExecutor)) {
            mediaExecutor.shutdown();
        }
    }
    @Override
    public void start() {
        running = true;
    }

    @Override
    public void stop() {
        running = false;
        cleanup();
    }

    @Override
    public boolean isRunning() {
        return running;
    }
}
