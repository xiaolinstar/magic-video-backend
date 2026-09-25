# 使用官方的 Eclipse Temurin 17 JRE 作为基础镜像，支持 amd64 和 arm64 架构
# 镜像体积更小、安全更新更及时
FROM eclipse-temurin:17.0.13_11-jre-jammy

ARG USER=xing.xiaolin@foxmail.com

# Jar包的版本，使用脚本从pom.xml中动态获取
ENV JAR_VERSION=0.0.1-SNAPSHOT

# 创建非 root 用户运行应用，提升容器安全性
RUN groupadd --system --gid 1001 magic && \
    useradd --system --uid 1001 --gid magic --no-create-home --shell /usr/sbin/nologin magic

WORKDIR /app

# 拷贝文件到Docker容器目录中
COPY --chown=magic:magic target/core-${JAR_VERSION}.jar /app/core-${JAR_VERSION}.jar

# 暴露9001端口
EXPOSE 9001

# 切换到非root用户
USER magic

# 生产环境
ENTRYPOINT ["java", "-jar", "/app/core-0.0.1-SNAPSHOT.jar"]