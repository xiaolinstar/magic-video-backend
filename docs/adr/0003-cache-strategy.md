# ADR-0003 · Spring Cache 接入与配置

- 状态：✅ 已落地
- 日期：2026-10-01

## 背景

`core/CacheConfig` 之前定义了 `CacheManager` bean，**但** `coreCacheConfig` Bean 里的 TTL 是 `Duration.ofMillis(10L)` 和 `Duration.ofMillis(30L)` —— 笔误，几乎等于关缓存。

更重要的是：`@EnableCaching` 注解从未启用 → `@Cacheable` 注解即使写了也无效。

## 改动

| 文件 | 内容 |
|---|---|
| `core/CacheConfig.java` | TTL 改为 10 分钟；删除无效的 `cacheConfiguration()` Bean；`GenericJackson2JsonRedisSerializer` 注册 `JavaTimeModule` 支持 Java 8 时间类型；启用 Jackson 多态类型校验，白名单为 `cn.xiaolin.*`、`java.util.*`、`java.time.*`、`java.math.*`；反序列化时忽略未知字段（兼容 VO 中只读派生属性） |
| `core/CoreApplication.java` | 加 `@EnableCaching` |
| `core/VideoServiceImpl.getVideoById` | 改为返回 `VideoVO` 并加 `@Cacheable(value="video", key="#id")`，视频不存在时返回 `null`，由 `disableCachingNullValues` 保证空值不写缓存 |

## 设计决策

### 1. TTL 选择 10 分钟

- 太短（毫秒级）失去缓存意义
- 太长（小时级）容易出现脏读
- 10 分钟对单点单机场景足够。视频元数据变更不频繁

### 2. 不缓存 `getVideoList()`

返回全表数据，不分页。缓存**无界 List 等于 OOM 风险**。待分页接口稳定后再考虑是否需要缓存首页。

### 3. Jackson 多态类型校验限定包

`GenericJackson2JsonRedisSerializer` 默认启用 polymorphic typing 才能反序列化具体类型，但**默认信任任何包**等于开着 Jackson RCE 后门。

把允许范围限定到白名单后：
- ✅ 自己服务的 VO/Entity 可反序列化（`cn.xiaolin.*`）
- ✅ 缓存值中出现的 JDK 集合/数值类型可反序列化（`java.util.*`、`java.math.*`，`BigDecimal` 非 final 会写入类型信息）
- ✅ 攻击者无法通过 Redis 推送任意类
- ❌ 需要序列化新的"非 Map 模型外的"类型时必须手动加包（一次性成本）

注：**不能**同时配置 `allowIfBaseType(Object.class)`。该判定会先于子类型白名单匹配所有基类，
导致 `allowIfSubType(...)` 完全失效（实测：非白名单类型仍会正常写入/读取类型信息）。

另外，缓存反序列化时需关闭 `FAIL_ON_UNKNOWN_PROPERTIES`：VO 里存在只有 getter 的派生属性
（如 `VideoVO#getEpisodeNumber`），序列化会写出但无法回填，否则缓存命中会直接抛异常。

### 4. 不缓存 `Optional`

`Optional` 会被 `GenericJackson2JsonRedisSerializer`（配合 `Jdk8Module`）**拆包**序列化：
写入时存的是被包裹对象本身，读回时得到的也是该对象而不是 `Optional`。
若 `@Cacheable` 方法以 `Optional<T>` 为返回类型，缓存命中时调用方会因类型转换失败抛出
`ClassCastException`（表现为偶发 500，取决于是否命中缓存）。

因此约定：需要缓存"可能不存在"的对象时，直接缓存具体对象，方法返回 `null`，
由 `disableCachingNullValues` 保证空值不落缓存。

## 备选

| 方案 | 否决理由 |
|---|---|
| Caffeine 本地缓存 | 单 JVM 内有效，但 HAS/Core/Gateway 容器隔离时各自一份，反而不一致 |
| Redis Hash 存 | 当前没需求用 Hash 存复杂结构，String 够用 |
| **Redis String + Jackson JSON + Spring Cache 抽象** | **采用**：最小依赖、Spring 原生、Redis 工具可观测 |

## 注意点

- Spring Cache 用 AOP 代理 → **同 Service 内部调用不走缓存**。当前 `getVideoById` 是被 controller 外部调用，没问题
- 缓存 key 加 `magic-video::` 前缀避免与项目其他 key 冲突
- 反序列化失败时 Spring Cache 默认抛 `SerializationException`，会被 `GlobalExceptionHandler` 兜底返回 500，但前端看到 500 体验差 → 建议 `errorHandler` 改为日志 + 失效（待办）
