# ADR-0002 · 列表接口统一分页

- 状态：✅ 已落地
- 日期：2026-10-01

## 背景

改造前所有 `list` 接口都是 `service.list()` 全表查：

- `core/ResourceController.java` 的 `/resource/all` `/recommend` `/banner` `/latest` `/classic` `/featured` **6 个端点** 全表扫
- `core/VideoServiceImpl.getVideoList()` 全表扫
- `core/VideoController.all()` 全表扫

视频表假设只有几十条还行；一旦增长就是 OOM / 接口超时。

## 决策

1. **新增 `PageQuery` 与 `PageResult`**，放在 `common-utils`，ORM 无关
2. **`common-utils/constant/PageConsts`** 定义默认值（page size = 20、max = 200）
3. **`VideoService.listVideoPage(PageQuery)` + `VideoSourceService.listSourcePage(PageQuery)`** 走分页
4. **Controller 接受 `current` + `size`** 参数，返回 `PageResult<T>`
6. **保留旧方法但 `@Deprecated`**，给 `CollectionServiceImpl.getVideoSet()` 等用
7. **响应封装 `PageResult`**，故意**不依赖** MyBatis-Plus，保持 common-utils 干净

## 影响

- ✅ 列表接口 QPS 稳定可控
- ✅ 老接口（`getVideoList` `/video/all`）标记 deprecated，不破坏调用方
- ✅ `PageResult` 隐藏 ORM 细节，未来换 ORM 不破坏 API 契约
- ⚠️ 客户端需要传递 `current` `size` —— 老客户端会拿到默认 20

## 备选方案

| 方案 | 否决理由 |
|---|---|
| 客户端 cursor 分页 | 复杂、客户端改动大、当前场景没必要 |
| 服务端按 hash 分片 | 单机无意义 |
| **Page + Sort 偏移分页** | **采用**：实现简单、对单机读场景足够 |

## 演进项

- 后续可加 `totalPages` 字段计算（当前基于 `size` 客户端可算）
- 后续可在 `PageQuery` 加 `cursor` 支持无限滚动
