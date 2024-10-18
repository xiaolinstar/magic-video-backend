package cn.xiaolin.has.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

/**
 * @author xingxiaolin xing.xiaolin@foxmail.com
 * @Description
 * @create 2024/10/13
 */
@Configuration
@RequiredArgsConstructor
@EnableScheduling
public class ScheduleTask {

    @Scheduled(fixedDelay = 10000)
    public void videoResourceScan() {
        System.out.println("Hello 定时任务");
    }
}
