package cn.xiaolin.auth;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author xlxing
 */
@SpringBootApplication(scanBasePackages = {"cn.xiaolin.auth", "cn.xiaolin.utils"})
@MapperScan(basePackages = {"cn.xiaolin.auth.domain.mapper"})
public class AuthApplication {

    public static void main(String[] args) {
        SpringApplication.run(AuthApplication.class, args);
    }


}
