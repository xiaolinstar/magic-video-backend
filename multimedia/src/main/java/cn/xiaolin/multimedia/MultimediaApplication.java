package cn.xiaolin.multimedia;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author xlxing
 */
@SpringBootApplication(scanBasePackages = {"cn.xiaolin.message", "cn.xiaolin.multimedia", "cn.xiaolin.utils"})
@RestController
@EnableAsync
public class MultimediaApplication {

	public static void main(String[] args) {
		SpringApplication.run(MultimediaApplication.class, args);
	}


}
