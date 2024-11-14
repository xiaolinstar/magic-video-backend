# 基于SpringCloud的视频点播微服务后端项目

> 个人搭建企业级微服务项目，DevOps实践，运维监控实践

本项目是一个使用Java技术栈实现的视频点播网站后端项目，全面涉及到了整个Java技术栈的主流技术，包括但不限于SpringBoot MySQL Redis MybatisPlus Nacos Dubbo。

此外，本项目还尝试建立了基于Jenkins的持续集成/持续部署，开发者只需关注业务代码，代码发布与上线已经流水线化。

---

## 更新日志

2024-09-22 构建docker-compose运行脚本

- 本地环境local：容器运行
- 开发环境dev：宿主机运行

2024-10-16 minio替换阿里云oss

- 本地缓存内容过多，导致启动容器失败，定期清理docker本地缓存
- .dev.env .local.env设置docker-compose环境变量，cli启动时增加参数`--env_file`
- 容器名称变更，magic-core magic-auth等
- hls微服务完善，支持视频编码为dash和hls
- 中间件数据文件、配置文件本地volume映射，新增volume文件夹统一管理
- 删除.gitignore文件中的test/目录，允许将单元测试添加到git仓库

## 服务文档

[Auth权限微服务](/auth/README.md)

[Gateway网关](/gateway/README.md)

[Core核心微服务](/core/README.md)

[Has视频转码微服务](/has/README.md)

[Multimedia多媒体微服务](/multimedia/README.md)


## 待办Todos

- [ ]  搭建Maven私有仓库Nexus
- [ ]  搭建私有代码托管仓库GitLab
- [ ]  配置文件与微服务解耦，密钥相关专门存储，包括数据库、Redis、RabbitMQ等
- [ ]  将视频存储在本地，替代阿里云oss，进行中
- [ ]  本地自动构建、自动运行脚本
- [ ]  开发环境关闭权限校验，完善代码灵活性
- [ ]  SpringCloudGateway及其他微服务组件healthcheck
- [X]  关闭docker-compose-local中暴露的本地端口，只支持容器内通信
- [ ]  各组件如Redis、MySQL、RabbitMQ连通性单元测试
- [X]  minio使用非9000 9001端口无法启动
- [ ]  单元测试依赖其他配置，如MySQL、Redis、RabbitMQ等
- [ ]  部署Prometheus监控Nginx流量
- [ ]  docker-compose-local中容器名变更
- [ ]  支持K8s容器编排和管理
- [ ]  快速启动文档更新，local和dev模式两种启动方式
- [ ]  容器镜像依赖tag，全部更新为确定版本，而不要使用默认latest
- [ ]  项目微服务关系架构图展示
- [ ]  重新安排pom依赖，应满足最小依赖原则
- [ ]  将aliyun oss的资源迁移到minio

---

## Java服务端开发基础知识

> 本项目使用Java技术栈，想要参与到本项目的开发工作，需要具备以下基础知识

### 必须掌握

Java基础知识：面相对象编程、集合、线程池
计算机基础知识：计算机网络、操作系统、Linux基础
Java企业级框架：SpringBoot
其他：git、docker

### 推荐掌握

Java基础知识：Java IO、JVM、Java锁

编程技巧：stream流、设计模式

数据库知识：Redis、MySQL

微服务：

- 分布式基础知识：CAP理论、分布式ID
- 数据库ORM工具：MybatisPlus
- 注册中心（Nacos、Eureka、Zookeeper）
- RPC（Dubbo、gRPC）
- 消息队列（RabbitMQ、Kafka）
- 配置中心（Nacos、ConfigServer）

DevOps：

- 持续集成：Jenkins
- 容器编排：docker-compose、k8s

---

## 快速开始

**克隆项目**

```shell
git clone git@github.com:xiaolinstar/magic-video-backend.git
```

**进入目录**

```
cd magic-video-backend
```

### 快速体验

> 组件和微服务全部容器化，使用docker-compose编排
> 配置环境设置为local

**docker compose启动**

修改本地参数

根目录下`.local.dev`中地址改为本地地址

在shell中输入

```shell
docker compose -f docker-compose-local.yaml --env_file .local.env up -d
```

容器卸载

> 不再使用的时候记得执行该指令以关闭所有容器

```shell
docker compose -f docker-compose-local.yaml --env_file .local.env down 
```

### 手动构建

> docker容器的迁移性与处理器体系架构有关，如linux/arm64 linux/amd64
>
> 基于本地源代码构建镜像，可以自动匹配宿主机架构

**编译项目(本地打包)**

```shell
# 跳过单元测试
mvn clean package -Dmaven.test.skip=true 
```

本项目中包含多个微服务，编译后生成的`jar`包，在每个微服务的`target`目录中。

**构建容器镜像**

```shell
docker build -t xxl1997/magic-auth:0.0.1-SNAPSHOT auth/.
docker build -t xxl1997/magic-gateway:0.0.1-SNAPSHOT gateway/.
docker build -t xxl1997/magic-multimedia:0.0.1-SNAPSHOT multimedia/.
docker build -t xxl1997/magic-core:0.0.1-SNAPSHOT core/.
```

**启动容器集群（本地开发环境）**

创建并启动

```shell
docker compose -f docker-compose-local.yaml --env_file .local.env up -d
```

容器卸载

```shell
docker compose -f docker-compose-local.yaml --env_file .local.env down
```

### 参与项目

> 开发环境dev，docker-compose启动中间件，本地IDEA启动微服务，将容器端口映射到宿主机，通过localhost访问

**docker compose启动dev环境**

修改`.dev.env`中环境变量

启动基于docker的依赖环境

```shell
docker compose -f docker-compose-dev.yaml --env_file .dev.env up -d
```

在IDEA中设置所有微服务启动参数`dev`

![ActiveProfile](assets/idea_active_profile.png)

依次启动以下微服务

1. auth
2. gateway
3. core
4. multimedia

---

## 项目配置

**端口配置原则**

当设计容器与宿主机端口映射时：

- 默认一致性映射，例如"9000:9000"
- 若存在多个容器时，端口号增加前缀5，如"59001:9001"

**微服务概览**


| 微服务/组件 | 名称               | 宿主机端口     | 容器端口       |
| ----------- | ------------------ | -------------- | -------------- |
| auth        | 鉴权微服务         | 8999           |                |
| gateway     | 网关微服务         | 9000           |                |
| core        | 核心微服务         | 9001           |                |
| multimedia  | 多媒体微服务       | 9002           |                |
| has         | http自适应流微服务 | 9003           |                |
| nacos       | 注册中心/配置中心  | 8848 9848 9849 | 8848 9848 9849 |
| mysql       | 关系型数据库       | 3306           | 3306           |
| auth-redis  | 鉴权分布式缓存     | 6379           | 6379           |
| redis       | 分布式缓存         | 6389           | 6379           |
| rabbitmq    | 消息队列           | 5672 15672     | 5672 15672     |
| minio       | 对象存储           | 9010 9011      | 9000 9001      |

---

## 服务介绍

微服务启动顺序：auth gateway core multimedia

### 鉴权微服务Auth

> 流行的鉴权框架主要有[SpringSecurity](https://spring.io/projects/spring-security/)、[SaToken](https://sa-token.cc/)、[Casbin](https://casbin.org/zh/)，本项目使用了文档对新手更友好的SaToken。

微服务下实现认证授权的方案：

- 基于Redis中间件实现分布式Session✅
- 无状态JsonWebToken（需要解决登出问题）

**环境依赖**

- 注册中心/配置中心：nacos
- 权限关系数据库：mysql
- SaToken分布式缓存：auth-redis

**对外服务提供Provider**

RPC服务提供者：dubbo

---

### 网关微服务Gateway

网关Gateway是后端服务的入口，访问后端请求统一由Gateway进行路由和调度。配合Nacos注册中心，实现负载均衡，将流量路由到**服务**，而不是**实例**。

认证授权也统一在网关处理，网关后面的微服务与权限服务解耦合。

**环境依赖**

- 注册中心/配置中心：nacos
- SaToken分布式缓存：auth-redis
- RPC服务提供者：dubbo

### 核心微服务Core

视频点播相关的视频、资源、导演、编剧等增删改查操作。

**环境依赖**

- 注册中心/配置中心：nacos
- 关系数据库：mysql
- 分布式缓存：redis
- 消息队列：rabbitmq

### 多媒体微服务Multimedia

图像、视频等前端上传资源逻辑处理，数据存储。

**环境依赖**

- 注册中心/配置中心：nacos
- 关系数据库：mysql
- 消息队列：rabbitmq
- 对象存储：minio

## DevOps

持续集成、持续部署
