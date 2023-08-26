# 基于SpringCloud的视频点播微服务后端项目

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
    - 用户名: root
    - 密码: 123456xxlBUPT
- Redis
  - 数据缓存: 47.94.104.34 (database 0)
  - 微服务认证授权: 111.229.38.208 (database 1)
- RabbitMQ
  - host: 47.94.104.34
  - username: xiaolin
  - password: 123456xxl
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
