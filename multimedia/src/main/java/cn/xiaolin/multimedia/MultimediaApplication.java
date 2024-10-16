package cn.xiaolin.multimedia;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author xingxiaolin xing.xiaolin@foxmail.com
 * EnableAsync 开启异步任务
 */
@SpringBootApplication(scanBasePackages = {"cn.xiaolin.message", "cn.xiaolin.multimedia", "cn.xiaolin.utils"})
@EnableAsync
public class MultimediaApplication {

	public static void main(String[] args) {
		SpringApplication.run(MultimediaApplication.class, args);
	}

}
