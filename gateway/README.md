# 微服务网关服务
SpringCloudGateway实现微服务流量转发
## 配置
- SaToken相关配置：Redis数据库，auth提供权限服务（gRPC调用）
- 权限控制资源路由配置
- 在application.yaml中配置路由转发规则
  - Nacos：路由到服务名
  - IP地址：被路由到的服务需开启跨域访问，与Gateway相同服务器使用localhost

## TODO
- auth服务gRPC端口号动态设置
- Nacos实现负载均衡访问各服务，而不是直接访问服务的IP地址
