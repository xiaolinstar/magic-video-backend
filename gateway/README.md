# 微服务网关服务

SpringCloudGateway实现微服务统一入口，提供统一鉴权和流量转发功能

## 更新日志

更新日志...

## TODO

- [ ]  auth服务gRPC端口号动态设置
- [X]  路由改为显式地址直连，不再需要 Nacos 做服务发现（2026-11-08 完成）
- [ ]  所有的环境变量，可以动态更改，需要保密的资源存储到服务器的环境变量中

## 环境依赖

- SaToken分布式数据库：Redis
- 权限来源：auth 内部 HTTP（`auth.base-url`，本地 compose 为 `http://magic-auth:8999`）

权限控制资源路由配置

在 `bootstrap-{profile}.yaml` 中配置路由转发规则：

- **直连 IP 地址**（无服务发现、无注册中心）：`uri: http://magic-auth:8999`
  （dev 为 `http://localhost:8999`）
- 被路由到的服务需开启跨域访问；与 Gateway 同服务器时用 `localhost`

> ⚠️ 客户端请求路径**不含** `/api/v1`，网关用 `PrefixPath=/api/v1` 在转发时补上。
> 详见 [ADR-0008](../docs/adr/0008-remove-nacos.md)。

## 参考

参考内容
