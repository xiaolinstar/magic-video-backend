# Http Adaptive Streaming视频转码
The following was discovered as part of building this project:

* No Docker Compose services found. As of now, the application won't start! Please add at least one service to the `compose.yaml` file.

## Todos

## 开发文档

**视频在服务端编码为hls和dash格式**

```mermaid
sequenceDiagram
    participant 前端服务
    participant core微服务
    前端服务 ->> core微服务: 1.新增或更新视频资源
    core微服务 ->> core微服务: 2.定时任务：扫描resource表，查找未编码视频资源
    core微服务 ->> MQ消息队列: 3.生成待办消息Producer
    MQ消息队列 ->> hls微服务: 4.消费待办消息
    hls微服务 ->> hls微服务: 5.mp4转hls和dash
    hls微服务 ->> core微服务: 6.视频编码完成，返回url
    core微服务 ->> core微服务: 7.更新resource表，填充hls和dash字段

```

同步关系：步骤7与步骤6同步
代码实现：远程关系调用，在hls微服务远程调用core微服务

