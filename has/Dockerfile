# 使用官方的OpenJDK-jdk-slim作为基础镜像，支持apt-get指令
FROM openjdk:17-jdk-slim

ARG USER=xing.xiaolin@foxmail.com

# Jar包的版本，使用脚本从pom.xml中动态获取
ENV JAR_VERSION=0.0.1-SNAPSHOT


# 更新包列表并安装 FFmpeg
RUN sed -i 's/deb.debian.org/mirrors.cloud.tencent.com/' /etc/apt/sources.list && \
    apt-get update && \
    apt-get install -y ffmpeg && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*

# 拷贝文件到Docker容器目录中
COPY target/has-${JAR_VERSION}.jar /app/has-${JAR_VERSION}.jar

# 暴露9001端口
EXPOSE 9001

# 生产环境
ENTRYPOINT ["java", "-jar", "/app/has-0.0.1-SNAPSHOT.jar"]
