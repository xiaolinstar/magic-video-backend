package cn.xiaolin.gateway;

import cn.dev33.satoken.stp.StpUtil;
import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author xlxing
 */
@SpringBootApplication(scanBasePackages = {"cn.xiaolin.gateway", "cn.xiaolin.api"})
@EnableDubbo
public class GatewayApplication {

	public static void main(String[] args) {
		SpringApplication.run(GatewayApplication.class, args);
	}

}
