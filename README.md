# 基于SpringCloud的视频点播微服务后端项目

## 快速启动
安装项目
```shell
git clone https://github.com/xlxingRun/magic-video-backend.git
```

编译项目(本地打包并跳过测试)
```shell
mvn clean install -Dmaven.test.skip=true
```
### 开发环境
本地依次启动auth multimedia core gateway四个微服务

### 生产环境
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

## 服务治理
注册中心 负载均衡 容错 配置中心 限流

RPC 服务治理 ESB

语言无关异构：
- gRPC
- Thrift IDL

## 快速开始
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


### 可选配置
- Nacos注册中心
- Nacos配置中心

## 微服务介绍

### 权限中心auth
权限中心采用SaToken集成统一微服务认证和授权，分布式Session解决跨服务访问。目前近支持简单的基于角色的权限控制。主要有三个实体构成：用户、角色、权限。
对外提供gRPC服务接口，可以供网关获取权限信息。

### 网关gateway
所有的服务请求都必须经过网关路由到特定的微服务。网关的主要职责：
- 负载均衡和路由分发
- 集中式认证和授权

### 核心服务core


