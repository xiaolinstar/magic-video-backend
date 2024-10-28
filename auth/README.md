# 认证授权微服务

认证授权功能基于Sa-Token实现

## 实现方式

[在WebFlux中注册过滤器](https://sa-token.cc/doc.html#/up/global-filter)

在Gateway微服务增加认证授权限制，通过rpc调用auth微服务

## TODO

增加一些个性化接口

- [ ]  踢人下线 kick-out
- [ ]  根据用户id，查询用户的所有角色名
- [ ]  根据用户id，查询用户的所有权限
- [ ]  根据用户id，查询用户的所有权限（包括角色间接用户的权限）
- [ ]  根据用户名，查询用户的所有角色名
- [ ]  根据用户名，查询用户的所有权限
- [ ]  根据用户名，查询用户的所有权限（包括角色间接用户的权限）
- [ ]  用户注册，验证用户名必须唯一
- [X]  password加密后数据插入MySQL中数据不一致：重复调用加密算法，已解决
- [ ]  SysPermission，view modify delete评估弃用，权限包含在name中

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

## 服务提供者Provider

在auth微服务中，用户显式进行权限相关操作，如登录、注册、资源查询等，当用户登录完成后，客户端将获取cookie或session id，之后的后端请求会进行隐式认证和授权。

整个后端微服务的入口是Gateway，因此需要在Gateway微服务进行，该过程是同步的，因此使用rpc方式实现。

auth提供以下3个服务：

- 根据用户ID获取用户的权限列表：getPermByUserId
- 根据用户ID获取用户的权限列表：getRoleByUserId
- 根据用户ID获取用户的权限列表，包括角色关联的权限：getPermWithRolesByUserId
