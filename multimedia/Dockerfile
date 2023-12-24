# 使用官方的OpenJDK 17作为基础镜像
FROM khipu/openjdk17-alpine:latest

# 维护者
MAINTAINER xlxing@bupt.edu.cn

RUN apk update && apk add ffmpeg

# 拷贝文件到Docker容器目录中
COPY target/multimedia-0.0.1-SNAPSHOT.jar /src/multimedia-0.0.1-SNAPSHOT.jar

# 阿里云OSS密钥
ENV OSS_ACCESS_KEY_ID xxx
ENV OSS_ACCESS_KEY_SECRET xxx
# 阿里云短信服务
ENV SMS_ACCESS_KEY_ID xxx
ENV SMS_ACCESS_SECRET xxx

# 暴露9002端口
EXPOSE 9002

ENTRYPOINT ["java", "-jar", "--spring.profiles.active=prod",
"--spring.cloud.nacos.discovery.ip=www.xingxiaolin.cn", "/src/multimedia-0.0.1-SNAPSHOT.jar"]
