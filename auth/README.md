# 认证授权微服务
认证授权功能基于Sa-Token实现

## TODO
增加一些个性化接口
- [ ] 踢人下线 kick-out
- [ ] 根据用户id，查询用户的所有角色名
- [ ] 根据用户id，查询用户的所有权限
- [ ] 根据用户id，查询用户的所有权限（包括角色间接用户的权限）
- [ ] 根据用户名，查询用户的所有角色名
- [ ] 根据用户名，查询用户的所有权限
- [ ] 根据用户名，查询用户的所有权限（包括角色间接用户的权限）
- [ ] 用户注册，验证用户名必须唯一
- [ ] password加密后数据插入MySQL中数据不一致

## 环境依赖
启动该应用前需先启动nacos、mysql和redis
- 注册中心Nacos：localhost:8848
- 权限相关MySQL数据库：localhost:3306
- SaToken分布式Redis数据库：localhost:6379


## 认证功能列表
包括用户注册、登陆、登出等功能。
功能清单
- AuthController：认证
  - 注册：register
  - 登陆：login
  - 登出：logout
- SysPermController：权限
  - 查询权限
  - 更新权限
  - 增加权限
  - 删除权限
- SysRoleController：角色
  - 查询角色
  - 更新角色
  - 新增角色
  - 删除角色
- SysRolePermController：角色权限关联
  - 查询角色权限
  - 更新角色权限
  - 新增角色权限
  - 删除角色权限
- SysUserController：用户
  - 查询用户
  - 更新用户
  - 新增用户
  - 删除用户
- SysUserPermController：用户权限关联关系
  - 查询用户权限
  - 更新用户权限
  - 新增用户权限
  - 删除用户权限
- SysUserRoleController：用户角色关联关系
  - 查询用户角色
  - 更新用户角色
  - 新增用户角色
  - 删除用户角色

## 授权
使用基于角色等权限管理方式，(用户-角色-权限)，同时支持(用户-权限)

