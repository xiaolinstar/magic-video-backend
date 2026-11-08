# ADR-0005 · MinIO 对象 key：UUID 化原始文件名

- 状态：✅ 已落地
- 日期：2026-10-01

## 背景

`multimedia/.../VideoServiceImpl.uploadVideoMinio(MultipartFile)` 之前的写法：

```java
.object(video.getOriginalFilename())   // 直接信任前端传入的文件名
```

**安全风险**：
- 攻击者上传 `../etc/passwd` 之类的文件名 → 覆盖 MinIO 上的对象
- 路径分隔符注入 → 写到恶意 bucket 子路径
- 同名文件互相覆盖

`ImageServiceImpl.buildSafeObjectKey()` 已经处理过同类问题，但 Video 模块没改。

## 决策

引入 `buildSafeObjectKey(originalFilename, videoType)` 方法：

1. 只保留扩展名的**字母数字部分**（最多 8 位）
2. 对象 key 改为 `<UUID>.<安全后缀>` 形式
3. extension 不可用时回退到 enum 自带后缀
4. **不可能**注入 `/` 或 `..`

## 影响

- ✅ 对象 key 永远安全，不可能路径遍历
- ✅ 同一文件上传两次得到不同 key（除非 UUID 碰撞，概率约 2^-122）
- ⚠️ **同名文件上传不再覆盖**——这通常是期望行为，但如果客户端期望"同名覆盖"则需要额外参数

## 备选

| 方案 | 否决理由 |
|---|---|
| 用 `originalFilename` 但加白名单字符校验 | 容易漏判，仍有风险 |
| 用 md5(filename + content) 作为 key | 上传同名文件覆盖问题；md5 容易伪造 |
| **UUID 随机 + 文件类型枚举兜底** | **采用**：绝对安全，文件名只决定扩展名 |

## 适用范围

- ✅ `uploadVideoMinio(MultipartFile, VideoTypeEnum)`（视频整体上传）
- ✅ `uploadVideoMinio(String, VideoTypeEnum)`（分片合并后整体上传）
- ✅ `ImageServiceImpl.imageUpload(MultipartFile)`（之前已修复，风格统一）
- ⏳ 上传签名 URL（如果之后做云 OSS）也需类似处理
