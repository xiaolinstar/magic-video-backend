package cn.xiaolin.gateway.config;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author xingxiaolin xlxing@bupt.edu.cn
 * @Description
 * @create 2023/8/13
 */
@Configuration
public class GrpcConfig {

    /**
     * Grpc Channel
     * @return 可复用的grpc通道
     */
    @Bean
    public ManagedChannel getChannel() {
        return ManagedChannelBuilder.forAddress("localhost", 9999)
                .usePlaintext()
                .build();
    }

}
