package cn.xiaolin.gateway.service;

import io.grpc.ManagedChannel;
import lombok.RequiredArgsConstructor;
import org.springframework.context.SmartLifecycle;
import org.springframework.stereotype.Service;

/**
 * @author xingxiaolin xlxing@bupt.edu.cn
 * @Description 程序关闭时清除已经开启的流
 * @create 2023/8/13
 */
@Service
@RequiredArgsConstructor
public class ShutdownServiceImpl implements SmartLifecycle {
    private volatile boolean running = false;

    private final ManagedChannel managedChannel;

    @Override
    public void start() {
        running = true;
    }

    @Override
    public void stop() {
        running = false;
        if (!managedChannel.isShutdown()) {
            managedChannel.shutdown();
        }
    }

    @Override
    public boolean isRunning() {
        return running;
    }
}
