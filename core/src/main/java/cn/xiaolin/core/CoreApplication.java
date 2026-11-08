package cn.xiaolin.core;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

/**
 * @author xingxiaolin xing.xiaolin@foxmail.com
 */
@SpringBootApplication(scanBasePackages = {"cn.xiaolin.core", "cn.xiaolin.utils"})
@MapperScan("cn.xiaolin.core.domain.mapper")
@EnableCaching
public class CoreApplication {

	public static void main(String[] args) {
		SpringApplication.run(CoreApplication.class, args);
	}

}
