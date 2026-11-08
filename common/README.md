# 公共模块

> **构建顺序（过渡期）**：根 reactor 已不再聚合 `common`。在其余 common 子模块尚未拆分完成前，干净本地仓库须先执行 `mvn -f common/pom.xml -DskipTests install`，再编译依赖 `common-*` 的根模块。


### 消息队列子模块
消息队列实体定义

### 公共工具子模块
公共使用的工具类和方法