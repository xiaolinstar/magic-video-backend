package cn.xiaolin.core;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.context.config.annotation.RefreshScope;



/**
 * @author xingxiaolin xing.xiaolin@foxmail.com
 */
@SpringBootApplication(scanBasePackages = {"cn.xiaolin.core", "cn.xiaolin.message", "cn.xiaolin.utils"})
@MapperScan("cn.xiaolin.core.domain.mapper")
@RefreshScope
public class CoreApplication {

	public static void main(String[] args) {
		SpringApplication.run(CoreApplication.class, args);
	}

}
