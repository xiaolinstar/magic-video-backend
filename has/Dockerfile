# 使用官方的 Eclipse Temurin 17 JRE 作为基础镜像
# 镜像体积更小、安全更新更及时
FROM eclipse-temurin:17.0.13_11-jre-jammy

ARG USER=xing.xiaolin@foxmail.com

# Jar包的版本，使用脚本从pom.xml中动态获取
ENV JAR_VERSION=0.0.1-SNAPSHOT

# 安装 FFmpeg（多媒体处理依赖）
# 1. 替换为腾讯镜像加速apt
# 2. 安装ffmpeg后清理apt缓存，减小镜像体积
# 3. 必须使用 root 完成 apt 安装，之后再切换到非 root 用户
RUN sed -i 's/deb.debian.org/mirrors.cloud.tencent.com/' /etc/apt/sources.list && \
    apt-get update && \
    apt-get install -y ffmpeg && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*

# 创建非 root 用户运行应用，提升容器安全性
RUN groupadd --system --gid 1001 magic && \
    useradd --system --uid 1001 --gid magic --no-create-home --shell /usr/sbin/nologin magic

WORKDIR /app

# 拷贝文件到Docker容器目录中
COPY --chown=magic:magic target/has-${JAR_VERSION}.jar /app/has-${JAR_VERSION}.jar

# 暴露9001端口
EXPOSE 9001

# 切换到非root用户
USER magic

# 生产环境
ENTRYPOINT ["java", "-jar", "/app/has-0.0.1-SNAPSHOT.jar"]