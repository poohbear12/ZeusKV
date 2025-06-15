# ZeusKV (分布式高性能KV存储系统)

<strong>项目描述: </strong>ZeusKV 是一款基于 Java
自主设计与实现的分布式键值数据库，具备网络通信、协议解析、数据存储、持久化、高可用集群等核心功能模块。项目从零开发，完整覆盖数据库关键组件，突出系统架构设计与核心功能实现能力，适用于高性能、高可扩展性场景。

### 1. 加载配置文件

- **步骤**：通过 `ConfigLoader` 类加载配置文件，默认加载 `zeus.yaml`。
- **代码**：
  ```java
  ConfigLoader configLoader = new ConfigLoader();
  ZeusConfig config = configLoader.getConfig();

### 2. 检查配置环境

- **步骤**：调用 `EnvCheck` 类的 `check` 方法，对加载的配置进行环境检查。
- **代码**：
  ```java
  EnvCheck.check(config);

### 3. 容器启动

- **步骤**：调用 `EngineContainer` 类的 `init` 方法，保证初始化一次,目前**StoreCore AOFManager**。
- **代码**：
  ```java
    EngineContainer.init(config);

### 4. 启动

- **步骤**：调用 `BannerPrinter` 类的 `printBannerFromFile` 方法，打印banner。
- **代码**：
  ```java
    BannerPrinter.printBannerFromFile();

### 5. Netty启动

- **步骤**：调用 `NettyServer` 类的 `start` 方法，启动Netty服务。
- **代码**：
  ```java
    KVServer server = new NettyServer(config);

## 重构优化部分

- [x] Netty启动优化
- [x] 启动流程规范

## 服务连接:

- [x] 基础连接实现
- [ ] 连接参数配置
- [ ] 连接权限管理
- [ ] 服务器可切换

## 协议解析:

- [x] 基础Resp协议解析
- [ ] Resp协议解析重构优化
- [ ] 协议扩展

## 基础数据结构

- [ ] 基础Redis数据结构
- [ ] 扩展数据结构

## AOF日志

- [x] 基础追加与恢复
- [ ] AOF重写
- [ ] 重构完善
- [ ] 优化

## RDB快照

- [ ] 基础实现与恢复
- [ ] save / bgsave
- [ ] 重构完善
- [ ] 优化

## 主从复制 / 集群搭建

- [ ] 基础主从复制集群搭建
- [ ] 重构完善
- [ ] 一致性协议优化

## 性能测试

- [ ] 基本读写性能测试
- [ ] 扩展数据结构性能测试
- [ ] AOF性能
- [ ] RDB性能
- [ ] 集群性能
