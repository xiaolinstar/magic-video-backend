# Grafana Alloy 配置说明

## 概述

本项目已从 Promtail 迁移到 Grafana Alloy 进行日志收集。Grafana Alloy 是 Grafana Labs 推出的新一代可观测性数据收集器，提供更强大的功能和更好的性能。

## 迁移内容

### 从 Promtail 迁移的功能

1. **日志收集**：收集以下服务的日志
   - magic-core
   - magic-auth
   - magic-gateway
   - magic-multimedia
   - magic-has

2. **日志处理**：
   - Spring Boot 日志格式解析
   - 提取时间戳、线程、日志级别、类名、方法名等信息
   - 标签化处理

3. **日志转发**：将处理后的日志发送到 Loki

### 配置文件结构

- `config.alloy`：主配置文件，包含所有日志收集和处理规则

### 主要组件

1. **discovery.file**：文件发现组件，定义日志文件路径和标签
2. **loki.source.file**：文件读取组件，从发现的文件中读取日志
3. **loki.process**：日志处理组件，解析和转换日志格式
4. **loki.write**：日志写入组件，将日志发送到 Loki

## 部署说明

### Docker Compose 配置

服务名已从 `magic-promtail` 更改为 `magic-alloy`：

```yaml
magic-alloy:
  image: grafana/alloy:latest
  container_name: magic-alloy
  volumes:
    - ./volume/alloy/config.alloy:/etc/alloy/config.alloy:ro
    - ./logs:/var/log
  environment:
    TZ: Asia/Shanghai
  networks:
    - magic-backend-network
  command:
    - "run"
    - "/etc/alloy/config.alloy"
    - "--server.http.listen-addr=0.0.0.0:12345"
  depends_on:
    - magic-loki
  healthcheck:
    test: [ "CMD", "curl", "-f", "http://localhost:12345/-/healthy" ]
    interval: 30s
    timeout: 10s
    retries: 3
    start_period: 40s
```

### 端口变更

- Promtail 使用端口 9080
- Alloy 使用端口 12345

### 健康检查

- Promtail: `http://localhost:9080/ready`
- Alloy: `http://localhost:12345/-/healthy`

## 验证迁移

1. 启动服务：
   ```bash
   docker-compose up -d magic-alloy
   ```

2. 检查服务状态：
   ```bash
   docker-compose ps magic-alloy
   ```

3. 查看日志：
   ```bash
   docker-compose logs magic-alloy
   ```

4. 访问 Alloy Web UI：
   ```
   http://localhost:12345
   ```

5. 在 Grafana 中验证日志是否正常收集

## 优势

相比 Promtail，Grafana Alloy 提供：

1. **更好的性能**：更高效的资源利用
2. **更强的功能**：支持更多数据源和处理方式
3. **统一配置**：可以同时处理 metrics、logs 和 traces
4. **更好的可观测性**：内置监控和调试功能
5. **向前兼容**：支持 Promtail 配置的迁移

## 故障排除

如果遇到问题，可以：

1. 检查配置文件语法
2. 查看容器日志
3. 验证文件路径和权限
4. 确认网络连接
5. 检查 Loki 服务状态