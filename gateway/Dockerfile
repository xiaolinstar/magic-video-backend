# 使用官方的OpenJDK 17作为基础镜像
FROM openjdk:17

# 维护者
MAINTAINER xlxing@bupt.edu.cn

# 拷贝文件到Docker容器目录中
COPY target/gateway-0.0.1-SNAPSHOT.jar /src/gateway-0.0.1-SNAPSHOT.jar

# 暴露9001端口
EXPOSE 9001

# 默认该镜像发布在阿里云服务器上
ENTRYPOINT ["java", "-jar", "--spring.profiles.active=prod",
"--spring.cloud.nacos.discovery.ip=www.xiaolin.fun", "/src/gateway-0.0.1-SNAPSHOT.jar"]
