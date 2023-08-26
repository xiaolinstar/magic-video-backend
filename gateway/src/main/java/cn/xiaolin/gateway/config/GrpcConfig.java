package cn.xiaolin.gateway.config;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import lombok.RequiredArgsConstructor;
import org.lognet.springboot.grpc.autoconfigure.GRpcServerProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author xingxiaolin xlxing@bupt.edu.cn
 * @Description
 * @create 2023/8/13
 */
@Configuration
@EnableConfigurationProperties(GRpcServerProperties.class)
@RequiredArgsConstructor
public class GrpcConfig {

    private final GRpcServerProperties gRpcServerProperties;

    /**
     * Grpc Channel
     * @return 可复用的grpc通道
     */
    @Bean
    public ManagedChannel managedChannel() {
        return ManagedChannelBuilder.forAddress(gRpcServerProperties.getInProcessServerName(), gRpcServerProperties.getPort())
                .usePlaintext()
                .build();
    }

}
