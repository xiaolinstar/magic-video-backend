# 视频点播核心微服务

核心微服务，视频资源相关的影视剧、地区、演员、编剧、制片人、评分等常规操作。


## TODO
- [ ] Redis分布式缓存，通过SpringCache实现资源自动缓存

## 环境依赖

- Nacos注册中心
- MySQL数据库，数据库名：magic-video 

## 概念设计

数据库设计

![核心框架](/assets/magic-video-core-arch[核心框架]-20241028112030.png)

## 远程过程调用

提供resource查询和保存服务: `DubboResourceService`

## 参考

1. Dubbo官方文档

