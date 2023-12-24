# Jenkins镜像，jdk17
FROM jenkins/jenkins:2.426.2-lts

# 维护者
MAINTAINER "xlxing@bupt.edu.cn"

# 切换为root用户
USER root

# 安装maven
RUN apt-get update && apt-get install -y maven

# 切换为jenkins用户
USER jenkins
