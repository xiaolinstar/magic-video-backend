package cn.xiaolin.has;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"cn.xiaolin.has", "cn.xiaolin.utils", "cn.xiaolin.message"})
public class HasApplication {

	public static void main(String[] args) {
		SpringApplication.run(HasApplication.class, args);
	}

}