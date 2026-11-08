# 视频点播核心微服务

核心微服务，视频资源相关的影视剧、地区、演员、编剧、制片人、评分等常规操作。


## TODO
- [ ] Redis分布式缓存，通过SpringCache实现资源自动缓存

## 环境依赖

- MySQL数据库，数据库名：magic-video
- Redis（业务缓存）

> **没有 Nacos**：本项目已彻底移除注册中心与配置中心。
> `has` 通过 Dubbo **直连** core（`dubbo://<host>:20881`），地址写在
> `bootstrap-{profile}.yaml` 里。详见 [ADR-0008](../docs/adr/0008-remove-nacos.md)。

## 概念设计

数据库设计

![核心框架](/assets/magic-video-core-arch[核心框架]-20241028112030.png)

## 远程过程调用

提供resource查询和保存服务: `DubboResourceService`

## 参考

1. Dubbo官方文档

