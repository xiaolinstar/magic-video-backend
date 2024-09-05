package cn.xiaolin.multimedia.config;

import com.baomidou.mybatisplus.core.incrementer.DefaultIdentifierGenerator;
import com.baomidou.mybatisplus.core.incrementer.IdentifierGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author xingxiaolin xing.xiaolin@foxmail.com
 * @Description ID生成器配置，默认使用MyBatisPlus中实现的雪花算法
 * @create 2023/8/6
 */
@Configuration
public class IdGeneratorConfig {

    @Bean
    public IdentifierGenerator multimediaIdGenerator() {
        return new DefaultIdentifierGenerator(0L, 1L);
    }
}
