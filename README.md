# 基于SpringCloud的视频点播微服务后端项目

> 个人搭建企业级微服务项目，DevOps实践，运维监控实践

本项目是一个使用Java技术栈实现的视频点播网站后端项目，全面涉及到了整个Java技术栈的主流技术，包括但不限于SpringBoot MySQL Redis MybatisPlus Nacos Dubbo。

此外，本项目还尝试建立了企业级的持续集成/持续部署，开发者只需关注业务代码，代码发布与上线已经流水线化。

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

## 待办Todos

- [ ]  搭建Maven私有仓库Nexus
- [ ]  搭建私有代码托管仓库GitLab
- [ ]  配置文件与微服务解耦，密钥相关专门存储，包括数据库、Redis、RabbitMQ等
- [ ]  将视频存储在本地，替代阿里云oss，进行中
- [ ]  本地自动构建、自动运行脚本
- [ ]  开发环境关闭权限校验，完善代码灵活性
- [ ]  SpringCloudGateway及其他微服务组件healthcheck
- [x]  关闭docker-compose-local中暴露的本地端口，只支持容器内通信
- [ ]  各组件如Redis、MySQL、RabbitMQ连通性单元测试
- [x]  minio使用非9000 9001端口无法启动
- [ ]  单元测试依赖其他配置，如MySQL、Redis、RabbitMQ等
- [ ]  部署Prometheus监控Nginx流量
- [ ]  docker-compose-local中容器名变更
- [ ]  支持K8s容器编排和管理
 
## 快速安装

**安装项目**

```shell
git clone https://github.com/xlxingRun/magic-video-backend.git
```

**进入目录**

```
cd magic-video-backend
```

**编译项目(本地打包)**

```shell
mvn clean package
```

本项目中包含多个微服务，编译后生成的`jar`包，在每个微服务目录的`target`中。

**打包容器镜像**

```shell
docker build -t xxl1997/magic-video:auth-0.0.1-SNAPSHOT auth/.
docker build -t xxl1997/magic-video:gateway-0.0.1-SNAPSHOT gateway/.
docker build -t xxl1997/magic-video:multimedia-0.0.1-SNAPSHOT multimedia/.
docker build -t xxl1997/magic-video:core-0.0.1-SNAPSHOT core/.
```

**启动容器集群（本地开发环境）**
创建并启动

```shell
docker compose -f docker-compose-local.yaml up -d
```

关闭并删除

```shell
docker compose -f docker-compose-local.yaml down
```

## 项目配置

### 端口号

**端口配置原则**
当设计容器与宿主机端口映射时：

- 默认一致性映射，例如"9000:9000"
- 若存在多个容器时，按顺序端口号依次+10

**微服务端口划分**

业务微服务

- auth: 8999
- gateway: 9000
- core: 9001
- multimedia: 90002

中间件容器

- nacos: 8848
- mysql: 3306
- redis:
  - 鉴权缓存: 6379
  - 数据缓存: 6389
- rabbitmq: 5672 15672

## 服务介绍

### 权限微服务Auth

比较主流的权限管理框架主要有[SpringSecurity](https://spring.io/projects/spring-security/)、[SaToken](https://sa-token.cc/)、[Casbin](https://casbin.org/zh/)，本项目使用了文档对新手更友好的SaToken。本项目是一个微服务项目，微服务下实现认证授权的方案由：

1. 基于Redis中间件实现分布式Session
2. 无状态JsonWebToken（需要解决登出问题）

> 环境依赖

- 注册中心：Nacos
- 配置中心：Nacos
- 权限数据库：MySQL
- SaToken分布式Session中间件：Redis
- RPC服务提供者：Dubbo

开发环境development

```

```

测试环境test

```

```

预发环境staging

```

```

生产环境production

```

```

### 网关微服务Gateway

微服务网管是后端流量的入口，访问后端资源不应该直接访问具体的服务，而是由Gateway进行路由和调度。同时配合Nacos注册中心，可以进行有效的负载均衡，将流量路由到**服务**，而不是**实例**。

认证授权也统一在网关处理，网关后面的微服务与权限服务解耦合。

> 环境依赖

- 注册中心：Nacos
- 配置中心：Nacos
- SaToken分布式Session中间件：Redis
- RPC服务提供者：Dubbo

### 核心微服务Core

主要包括视频点播相关的视频、资源、导演、编剧等信息，提供基本的增删改查操作，主要业务逻辑在Core中实现。

> 环境依赖

- 注册中心：Nacos
- 配置中心：Nacos
- 核心数据库：MySQL
- 分布式缓存：Redis
- 消息队列：RabbitMQ

### 多媒体微服务Multimedia

## 3. 快速启动

### 3.1 本地开发环境

#### 1. 启动docker-compose

本地开发环境依赖中间件：mysql redis rabbitmq等，使用docker-compose编排启动

#### 2. 启动四个微服务项目

建议直接在Idea中按下列顺序启动：

1. auth, localhost:8999
2. gateway, localhost:9001
3. core, localhost:9000
4. multimedia, localhost:9002

#### 3. Todo

- [X]  启用nacos配置中心，并在启动时导入默认应用配置
- [X]  微服务容器化
- [X]  将视频存储在本地，替代阿里云oss

### 3.2 生产环境（未完成）

每一个jar包位于子项目的/target/xxx.jar，构建Docker镜像
使用buildx构架多平台版本镜像

```shell
docker buildx build --platform linux/amd64,linux/arm64 -t xxl1997/magic-video:backend-auth auth/.
docker buildx build --platform linux/amd64,linux/arm64 -t xxl1997/magic-video:backend-core core/.
```

启动顺序

1. auth: www.xiaolin.fun
2. gateway: www.xiaolin.fun
3. core: www.xingxiaolin.cn
4. multimedia: www.xingxiaolin.cn

注册中心 负载均衡 容错 配置中心 限流

RPC 服务治理 ESB

语言无关异构：

- gRPC
- Thrift IDL
- Web服务端口号

  - auth: 8999
  - core: 9000
  - gateway: 9001
  - multimedia: 9002
- gRPC服务端口号

  - auth: 9999
- MySQL

  - 腾讯云TD-SQL兼容MYSQL5.7
- Redis

  - 数据缓存: www.xiaolin.fun
    - 开发环境 database 0
    - 生产环境 database 2
  - 微服务认证授权: www.xingxiaolin.cn
    - 开发环境 database 0
    - 生产环境 database 2
- RabbitMQ

  - host: www.xiaolin.fun
  - virtual-host: /media
  - connection-timeout: 15000

### 服务依赖

Auth:

- Redis鉴权使用
- MySQL
- gRPC生产者
  Core:
- MySQL
- Redis
- RabbitMQ消费
  Gateway:
- Redis鉴权使用
- gRPC消费者
  Multimedia:
- FFMpeg
- FFProbe
- 阿里云OSS
- RabbitMQ生产
- Redis: （TODO）判断视频是否已经存在于服务器

### 开发环境Docker-compose快速启动

容器名称：

- 注册中心、配置中心nacos: magic-dev-nacos
- 业务数据库mysql: magic-dev-mysql
- 消息队列rabbitmq: magic-dev-rabbitmq
- 鉴权缓存redis: magic-dev-auth-redis
-

### 可选配置

- Nacos注册中心
- Nacos配置中心

## 4. 微服务介绍

### 权限中心auth

权限中心采用SaToken集成统一微服务认证和授权，分布式Session解决跨服务访问。目前近支持简单的基于角色的权限控制。主要有三个实体构成：用户、角色、权限。
对外提供gRPC服务接口，可以供网关获取权限信息。

### 网关gateway

所有的服务请求都必须经过网关路由到特定的微服务。网关的主要职责：

- 负载均衡和路由分发
- 集中式认证和授权

### 核心服务core

## 5.持续集成/持续部署
