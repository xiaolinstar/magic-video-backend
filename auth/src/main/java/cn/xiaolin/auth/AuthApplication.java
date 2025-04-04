package cn.xiaolin.auth;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * @author xingxiaolin xing.xiaolin@foxmail.com
 */
@SpringBootApplication(scanBasePackages = {"cn.xiaolin.auth", "cn.xiaolin.utils", "cn.xiaolin.db"})
@MapperScan(basePackages = {"cn.xiaolin.auth.domain.mapper"})
@EnableDubbo
public class AuthApplication {
    public static void main(String[] args) {
        SpringApplication.run(AuthApplication.class, args);
    }


}
