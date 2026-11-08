# ADR-0004 · Sa-Token 白名单收紧：移除 `/auth/logout` `/auth/kick-out`

- 状态：✅ 已落地
- 日期：2026-10-01

## 背景

`gateway/.../config/SaTokenConfig.java` 的 `PUBLIC_PATHS` 数组里包含：

```java
"/auth/logout",
"/auth/kick-out",
```

**含义**：所有未登录用户都能调这两个接口。

问题：

1. `/auth/logout` 用 `StpUtil.logout()` —— 未登录时无 session，逻辑上是 no-op，但绕过鉴权链条本身就是糟糕的设计
2. `/auth/kick-out` 是**管理员功能**，未登录用户也能调用意味着任何人都能强制任何用户下线 → **可达严重安全事件**

## 决策

从白名单中移除 `/auth/logout` 和 `/auth/kick-out`。两者都要求调用方已经登录。

## 影响

- ✅ 未登录用户调 `/auth/logout` → `NotLoginException` → 401
- ✅ 未登录用户调 `/auth/kick-out` → `NotLoginException` → 401
- ⚠️ 前端如果有"未登录点退出按钮就跳登录页"的逻辑，会改成 401 而非空成功 —— 通常可接受

## 备选

| 方案 | 否决理由 |
|---|---|
| 给 `/auth/logout` 单独写一个"无 session 也 200"的兼容 | 重复调用 `StpUtil.logout()` 没意义反而增加代码复杂度 |
| 给 `/auth/kick-out` 单独鉴权为管理员角色 | 当前没有管理员角色概念，要做就引入完整 RBAC，超范围 |
| **两个都走全局 `StpUtil.checkLogin()`** | **采用**：保持简单，统一鉴权链 |

## 配套修复

修订 `addExclude(List.of(PUBLIC_PATHS))` 改为 `addExclude(PUBLIC_PATHS)` —— Sa-Token 1.38.0 的 `addExclude` 接受 `String...` 不接受 `List<String>`，原写法会编译失败。
