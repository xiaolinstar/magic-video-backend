# 认证授权微服务

认证授权功能基于 Sa-Token 实现

## 实现方式

[在WebFlux中注册过滤器](https://sa-token.cc/doc.html#/up/global-filter)

在 Gateway 微服务增加认证授权限制，通过 rpc 调用 auth 微服务

## TODO

根据业务需求来实现类及其功能

增加一些个性化接口


- [X]  踢人下线 kickOut
- [X]  根据用户id，查询用户的所有角色名DubboUserPermService:getRoleByUserId
- [X]  根据用户id，查询用户的所有权限 DubboUserPermService:getPermByUserId
- [X]  根据用户id，查询用户的所有权限（包括角色间接用户的权限）DubboUserPermService:getPermWithRolesByUserId
- [ ]  根据用户名，查询用户的所有角色名
- [ ]  根据用户名，查询用户的所有权限
- [ ]  根据用户名，查询用户的所有权限（包括角色间接用户的权限）
- [X]  查询具有某角色的所有用户 SysUserService:listUsersByRoleId
- [X]  查询具有某权限的所有用户 SysUserService:listUsersByPermId
- [X]  查询具有某权限的所有用户，包括基于角色间接拥有权限的用户 SysUserService:listUsersWithRoleByPermId
- [ ]  用户注册，校验用户名必须唯一
- [X]  password加密后数据插入MySQL中数据不一致：重复调用加密算法，已解决
- [ ]  SysPermission，view modify delete 评估弃用，权限包含在name中
- [ ]  理清楚方法与类之间的关系
- [ ]  Dubbo服务端口号设置，替代-1端口号


## 环境依赖

启动该应用前需先启动nacos、mysql和鉴权redis

## 概念设计

数据库设计

![权限中心](/assets/magic-video-auth-arch[权限中心架构]-20241028100452.png)


## 认证和授权

`AuthController`包括用户注册register、登陆login、登出logout功能。其他常规服务如下表：


| 控制器                | 说明         | 增 | 删 | 改 | 查 |
| --------------------- | ------------ | -- | -- | -- | -- |
| SysPermController     | 权限         | ✅ | ✅ | ✅ | ✅ |
| SysRoleController     | 角色         | ✅ | ✅ | ✅ | ✅ |
| SysRolePermController | 角色权限关联 | ✅ | ✅ | ✅ | ✅ |
| SysUserController     | 用户         | ✅ | ✅ | ✅ | ✅ |
| SysUserPermController | 用户权限关联 | ✅ | ✅ | ✅ | ✅ |
| SysUserRoleController | 用户角色关联 | ✅ | ✅ | ✅ | ✅ |

## 服务提供者 Provider

在auth微服务中，用户显式进行权限相关操作，如登录、注册、资源查询等，当用户登录完成后，客户端将获取cookie或session id，之后的后端请求会进行隐式认证和授权。

整个后端微服务的入口是Gateway，因此需要在Gateway微服务进行，该过程是同步的，因此使用rpc方式实现。

auth 提供以下 3 个函数服务：

| 函数名 | 功能描述 |
|-----|------|
| getPermByUserId | 根据用户ID获取用户的权限列表 |
| getRoleByUserId | 根据用户ID获取用户的权限列表 |
| getPermWithRolesByUserId | 根据用户ID获取用户的权限列表，包括角色关联的权限 |


## 参考

