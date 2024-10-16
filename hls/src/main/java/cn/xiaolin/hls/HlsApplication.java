package cn.xiaolin.hls;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"cn.xiaolin.hls", "cn.xiaolin.utils", "cn.xiaolin.message"})
public class HlsApplication {

	public static void main(String[] args) {
		SpringApplication.run(HlsApplication.class, args);
	}

}
