# 使用官方的OpenJDK 17.0.2作为基础镜像，支持amd64和arm64架构
FROM openjdk:17.0.2

ARG USER=xing.xiaolin@foxmail.com

# Jar包的版本，使用脚本从pom.xml中动态获取
ENV JAR_VERSION=0.0.1-SNAPSHOT

# 拷贝文件到Docker容器目录中
COPY target/core-${JAR_VERSION}.jar /app/core-${JAR_VERSION}.jar

# 暴露9000端口
EXPOSE 9000

# 生产环境
ENTRYPOINT ["java", "-jar", "/app/core-0.0.1-SNAPSHOT.jar"]
