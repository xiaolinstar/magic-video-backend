# Http Adaptive Streaming视频转码

将视频转码为dash和hls格式

## 更新日志

2024-10-28 更新文档目录结构


## Todos

- [ ]  ffmpeg转码dash
- [ ]  ffmpeg转吗hls
- [ ]  mysql依赖是需要的吗？

## 开发文档

**视频在服务端编码为hls和dash格式**

```mermaid
sequenceDiagram
    participant 前端服务
    participant core微服务
    participant MQ消息队列
    participant has微服务
    participant multimedia微服务
    前端服务 ->> multimedia微服务: 1.1 上传图像、视频
    multimedia微服务 ->> multimedia微服务: 1.2 md5校验，视频存储到Minio
    multimedia微服务 ->> 前端服务: 1.3 成功，返回资源url；失败，返回错误信息
    前端服务 ->> core微服务: 2.1 新增或更新视频资源
    core微服务 ->> core微服务: 2.2 视频存储到Minio，写resource表
    core微服务 ->> 前端服务: 2.3 返回视频上传状态：成功or失败
    core微服务 ->> MQ消息队列: 2.4 生成转码待办消息Producer
    MQ消息队列 ->> has微服务: 2.5 消费待办消息Consumer
    has微服务 ->> has微服务: 2.6 根据url获取视频资源，并使用ffmpeg编码为hls或dash
    has微服务 ->> core微服务: 2.7 rpc调用core微服务写resource表

```

同步关系：步骤7与步骤6同步


## 参考

参考内容