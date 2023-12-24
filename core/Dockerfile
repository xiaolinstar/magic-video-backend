# 使用官方的OpenJDK 17作为基础镜像
FROM openjdk:17

# 维护者
MAINTAINER xlxing@bupt.edu.cn

# 拷贝文件到Docker容器目录中
COPY target/core-0.0.1-SNAPSHOT.jar /src/core-0.0.1-SNAPSHOT.jar

# 暴露9000端口
EXPOSE 9000

# 生产环境，发布在腾讯云服务器上
ENTRYPOINT ["java", "-jar", "--spring.profiles.active=prod",
"--spring.cloud.nacos.discovery.ip=www.xingxiaolin.cn", "/src/core-0.0.1-SNAPSHOT.jar"]
