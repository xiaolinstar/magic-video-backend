# 已废弃

这些 YAML 是早期 Kubernetes 草稿，不能部署当前服务。

- 镜像仍是 `xxl1997/magic-video:*`，与 compose 使用的 `xiaolinstar/magic-*` 不一致
- 端口和 selector 与现有服务对不上，Deployment 起不来
- 没有 has，也没有可用的 ConfigMap、Secret、Ingress

本轮不交付可运行的 K8s。现行部署是 `docker-compose-*.yaml` 和 `mini-local.yaml`。不要对这些文件执行 `kubectl apply`。
