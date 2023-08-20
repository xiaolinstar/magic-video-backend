package cn.xiaolin.core;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


/**
 * @author xlxing
 */
@SpringBootApplication(scanBasePackages = {"cn.xiaolin.core", "cn.xiaolin.message", "cn.xiaolin.utils"})
@MapperScan("cn.xiaolin.core.domain.mapper")
@RefreshScope
public class CoreApplication {

	public static void main(String[] args) {
		SpringApplication.run(CoreApplication.class, args);
	}

	@Value("${pattern.dateformat}")
	private String dateformat;

	@GetMapping("/now")
	public String currentDateTime() {
		return LocalDateTime.now().format(DateTimeFormatter.ofPattern(dateformat));
	}
}
