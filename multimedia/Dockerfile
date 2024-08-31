# 使用官方的OpenJDK 17作为基础镜像
FROM openjdk:17-jdk-slim

ARG USER=xing.xiaolin@foxmail.com

# 镜像环境变量：
# 本地开发环境：dev
# 开发环境：dev
# 测试环境：test
# 预发环境：staging
# 生产环境：prod
ENV ENVIRONMENT=local

# Jar包的版本，使用脚本从pom.xml中动态获取
ENV JAR_VERSION=0.0.1-SNAPSHOT

# 阿里云OSS密钥
ENV OSS_ACCESS_KEY_ID tofill
ENV OSS_ACCESS_KEY_SECRET tofill
# 阿里云短信服务
ENV SMS_ACCESS_KEY_ID tofill
ENV SMS_ACCESS_SECRET tofill

# 维护者
MAINTAINER ${USER}

# 更新包列表并安装 FFmpeg
RUN apt-get update && \
    apt-get install -y ffmpeg && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*


# 拷贝文件到Docker容器目录中
COPY target/multimedia-${JAR_VERSION}.jar /app/multimedia-${JAR_VERSION}.jar

# 暴露9002端口
EXPOSE 9002

# 生产环境
ENTRYPOINT ["java", "-jar", "/app/multimedia-0.0.1-SNAPSHOT.jar"]
