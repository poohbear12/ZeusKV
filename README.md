# ZeusKV (分布式高性能KV存储系统)

<strong>项目描述: </strong>ZeusKV 是一款基于 Java
自主设计与实现的分布式键值数据库，具备网络通信、协议解析、数据存储、持久化、高可用集群等核心功能模块。项目从零开发，完整覆盖数据库关键组件，突出系统架构设计与核心功能实现能力，适用于高性能、高可扩展性场景。
# 性能对比（KVObject/KVByte/KVBytes）
**KVObject(K&V采用Object) / KVByte(K采用Object，V采用byte[]) / KVBytes(K&V均采用byte[]) 编解码Kryo**
## 1. 序列化性能对比（以 KVObject 为基准,us/op(一次操作需要多少微秒/越小性能越好)）
## 2. 反序列化性能对比（以 KVObject 为基准）
## 3. 读写操作性能对比（以 KVBytes 为基准）
## 4. 内存占用对比（以 KVObject 为基准）
| 数据量      | KVObject 内存占用(MB) | KVByte 内存占用(MB) | KVBytes 内存占用(MB) | KVObject 节省百分比(%) | KVByte 节省百分比(%) | KVBytes 节省百分比(%) |
| :------- | :---------------- | :-------------- | :--------------- | :---------------- | :-------------- | :--------------- |
| 1000     | 0.36              | 0.14            | 0.08             | 100.0             | 61.1            | 77.8             |
| 10000    | 3.55              | 1.10            | 0.87             | 100.0             | 69.0            | 75.5             |
| 100000   | 35.63             | 11.06           | 8.77             | 100.0             | 68.9            | 75.4             |
| 1000000  | 355.21            | 111.13          | 88.09            | 100.0             | 68.7            | 75.2             |
| 10000000 | 3550.18           | 1112.3          | 887.4            | 100.0             | 68.7            | 75.0             |
### 测试环境
| **参数**      | **详细信息**                   |
| :---------- | :------------------------- |
| **CPU 型号**  | Intel Core i5-9400 (九代 I5) |
| **内存**      | 16GB，频率 2666MHz            |
| **堆内存设置**   | 最大 8096MB                  |
| **操作系统**    | Windows 11 版本 22H2         |
| **Java 版本** | JDK 21.0.1                 |
| **IDEA 版本** | 2023.3.5                   |
| **项目构建工具**  | Maven 3.8.1                |

## 启动流程

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
- [x] 连接参数配置
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


