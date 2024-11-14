package cn.xiaolin.multimedia.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

/**
 * @author xingxiaolin xing.xiaolin@foxmail.com
 * @Description 定时任务，扫描数据库Resource表
 * @create 2024/10/12
 */
@Configuration
@EnableScheduling
@RequiredArgsConstructor
public class ScheduleTask {

    public void videoResourceScan() {
        System.out.println("Hello 定时任务");
    }
}
