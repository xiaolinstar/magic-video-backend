# 使用官方的OpenJDK 17作为基础镜像
FROM openjdk:17

# 维护者
MAINTAINER xlxing@bupt.edu.cn

# 拷贝文件到Docker容器目录中
COPY target/auth-0.0.1-SNAPSHOT.jar /src/auth-0.0.1-SNAPSHOT.jar

# 暴露8999端口
EXPOSE 8999

# 生产环境 注册服务到阿里云服务器
ENTRYPOINT ["java", "-jar", "--spring.profiles.active=prod",
"--spring.cloud.nacos.discovery.ip=www.xiaolin.fun", "/src/auth-0.0.1-SNAPSHOT.jar"]
