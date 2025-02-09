# Http Adaptive Streaming视频转码

将视频转码为dash和hls格式

## 更新日志

2024-10-28 更新文档目录结构


## Todo

- [ ]  ffmpeg 脚本优化
- [ ]  ffmpeg 转码 dash
- [ ]  ffmpeg 转码 hls
- [ ]  mysql 依赖是需要的吗？不需要的话删除

## 开发文档

**视频在服务端编码为 hls 和 dash 格式**

```mermaid
sequenceDiagram
    participant 前端服务
    participant core微服务
    participant MQ消息队列
    participant has微服务
    participant multimedia微服务
    前端服务 ->> multimedia微服务: 1.1 上传图像、视频
    multimedia微服务 ->> multimedia微服务: 1.2 md5 校验，视频存储到 Minio
    multimedia微服务 ->> 前端服务: 1.3 成功，返回资源 url；失败，返回错误信息
    前端服务 ->> core微服务: 2.1 新增或更新视频资源
    core微服务 ->> core微服务: 2.2 视频存储到 Minio，写 resource 表
    core微服务 ->> 前端服务: 2.3 返回视频上传状态：成功or失败
    core微服务 ->> MQ消息队列: 2.4 生成转码待办消息 Producer
    MQ消息队列 ->> has微服务: 2.5 消费待办消息 Consumer
    has微服务 ->> has微服务: 2.6 根据 url 获取视频资源，并使用 ffmpeg 编码为 hls 或 dash
    has微服务 ->> core微服务: 2.7 rpc 调用 core 微服务写 resource 表
```

待思考的问题：

1. 大视频上传需要**分片上传**，需要在前端分片，如何保证后端接收到完整的分片？
2. 视频转码需要**进度**，如何保证转码进度可以实时反馈给前端？
3. 视频转码需要**异步**，转码完成后是否需要通知前端，如何实现？
4. 后端接收到视频资源后，需要合并吗？
5. 视频存储到 minio 时会自动分片，ffmpeg 编码 dash 和 hls 需完整视频块？服务端存储合并视频，ffmpeg 分片后传输到 minio，然后在服务端删除原视频。


## 参考

