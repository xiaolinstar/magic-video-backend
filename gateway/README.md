# 微服务网关服务

SpringCloudGateway实现微服务统一入口，提供统一鉴权和流量转发功能

## 更新日志

更新日志...

## TODO

- [ ]  auth服务gRPC端口号动态设置
- [ ]  Nacos实现负载均衡访问各服务，而不是直接访问服务的IP地址，Done
- [ ]  所有的环境变量，可以动态更改，需要保密的资源存储到服务器的环境变量中，也可以在Nacos中设置

## 环境依赖

- SaToken分布式数据库：Redis
- 权限Dubbo-Provider：auth-rpc

权限控制资源路由配置

在application.yaml中配置路由转发规则

- Nacos：路由到服务名
- IP地址：被路由到的服务需开启跨域访问，与Gateway相同服务器使用localhost

## 参考

参考内容
