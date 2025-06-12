package com.cc.config.loader;

import com.cc.config.entity.ZeusConfig;
import com.cc.config.entity.NodeConfig;
import com.cc.config.entity.NodeType;
import com.cc.config.entity.AofConfig;
import com.cc.config.entity.ZdbConfig;
import lombok.extern.slf4j.Slf4j;
import org.yaml.snakeyaml.Yaml;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * @program: zeus-kv
 * @description: 配置类加载器
 * @author: ccstar
 * @create: 2025-06-09  23:48
 **/
@Slf4j
public class ConfigLoader {

    private ZeusConfig zeusConfig;

    private static final String DEFAULT_CONFIG_NAME = "zeus.yml";

    public ConfigLoader() {
        loadYaml(DEFAULT_CONFIG_NAME);
    }

    public ConfigLoader(String fileName) {
        loadYaml(fileName);
    }

    /**
     * 默认加载
     */
    public void loadYaml() {
        loadYaml(DEFAULT_CONFIG_NAME);
    }

    /**
     *  指定文件名加载
     * @param fileName
     */
    public void loadYaml(String fileName) {
        log.info("Zeus服务启动中!");
        log.info("正在加载配置文件{}",fileName);
        Yaml yaml = new Yaml(new KebabCaseConstructor(ZeusConfig.class));
        InputStream io = this.getClass().getClassLoader().getResourceAsStream(fileName);
        this.zeusConfig  = yaml.load(io);
        fillDefaultValues(zeusConfig); // 填充值
//        System.out.println(configToString(zeusConfig));
        log.info("配置文件{}",fileName + "加载成功!");
    }

    public String configToString(ZeusConfig config) {
        if (config == null) {
            return "ZeusConfig is null";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("=== Zeus Configuration ===\n");

        // 节点配置
        sb.append("\nNodes:\n");
        if (config.getNodes() == null || config.getNodes().isEmpty()) {
            sb.append("  - No nodes configured\n");
        } else {
            for (int i = 0; i < config.getNodes().size(); i++) {
                NodeConfig nodeConfig = config.getNodes().get(i);
                sb.append("  Node ").append(i + 1).append(":\n");
                sb.append("    Name: ").append(nodeConfig.getName()).append("\n");
                sb.append("    Addr: ").append(nodeConfig.getAddr()).append("\n");
                sb.append("    Port: ").append(nodeConfig.getPort()).append("\n");
                sb.append("    Type: ").append(nodeConfig.getType()).append("\n");
                sb.append("    Cluster ID: ").append(nodeConfig.getClusterId()).append("\n");
                sb.append("    Heartbeat Interval: ").append(nodeConfig.getHeartbeatInterval()).append("\n");
                sb.append("    Failure Detection Timeout: ").append(nodeConfig.getFailureDetectionTimeout()).append("\n");
                sb.append("    Max Connections: ").append(nodeConfig.getMaxConnections()).append("\n");
            }
        }

        // AOF配置
        sb.append("\nAOF Configuration:\n");
        if (config.getAof() == null) {
            sb.append("  - AOF not configured\n");
        } else {
            AofConfig aof = config.getAof();
            sb.append("  Enable: ").append(aof.isEnable()).append("\n");
            sb.append("  Filename: ").append(aof.getFilename()).append("\n");
            sb.append("  Preallocate Size: ").append(aof.getPreallocateSize()).append("\n");
            sb.append("  Flush Interval: ").append(aof.getFlushInterval()).append("\n");
            sb.append("  Large Command Threshold: ").append(aof.getLargeCmdThreshold()).append("\n");
            sb.append("  Backpressure Threshold: ").append(aof.getBackpressureThreshold()).append("\n");
            sb.append("  Queue Size: ").append(aof.getQueueSize()).append("\n");
            sb.append("  Min Batch Size: ").append(aof.getMinBatchSize()).append("\n");
            sb.append("  Max Batch Size: ").append(aof.getMaxBatchSize()).append("\n");
            sb.append("  Min Batch Timeout: ").append(aof.getMinBatchTimeout()).append("\n");
            sb.append("  Max Batch Timeout: ").append(aof.getMaxBatchTimeout()).append("\n");
        }

        // ZDB配置
        sb.append("\nZDB Configuration:\n");
        if (config.getZdb() == null) {
            sb.append("  - ZDB not configured\n");
        } else {
            ZdbConfig zdb = config.getZdb();
            sb.append("  Enable: ").append(zdb.isEnable()).append("\n");
        }

        return sb.toString();
    }

    private void fillDefaultValues(ZeusConfig zeusConfig) {
        // 处理ZeusConfig的各个字段
        if (zeusConfig == null) {
            zeusConfig = new ZeusConfig();
        }

        // 处理nodes字段
        if (zeusConfig.getNodes() == null) {
            zeusConfig.setNodes(new ArrayList<>());
        }

        // 为每个节点填充默认值
        if (zeusConfig.getNodes().isEmpty()) {
            NodeConfig defaultNodeConfig = new NodeConfig();
            fillDefaultNodeValues(defaultNodeConfig);
            zeusConfig.getNodes().add(defaultNodeConfig);
        } else {
            for (NodeConfig nodeConfig : zeusConfig.getNodes()) {
                fillDefaultNodeValues(nodeConfig);
            }
        }

        // 处理aof字段
        if (zeusConfig.getAof() == null) {
            zeusConfig.setAof(new AofConfig());
        }
        fillDefaultAofValues(zeusConfig.getAof());

        // 处理zdb字段
        if (zeusConfig.getZdb() == null) {
            zeusConfig.setZdb(new ZdbConfig());
        }
        fillDefaultZdbValues(zeusConfig.getZdb());
    }

    // 为NodeConfig填充默认值
    private void fillDefaultNodeValues(NodeConfig nodeConfig) {
        if (nodeConfig.getName() == null) nodeConfig.setName("default_node");
        if (nodeConfig.getAddr() == null) nodeConfig.setAddr("localhost");
        if (nodeConfig.getPort() <= 0) nodeConfig.setPort(6379); // 端口号保持正数
        if (nodeConfig.getType() == null) nodeConfig.setType(NodeType.SINGLE);
        if (nodeConfig.getClusterId() == 0) nodeConfig.setClusterId(-1);
        if (nodeConfig.getHeartbeatInterval() <= 0) nodeConfig.setHeartbeatInterval(-1);
        if (nodeConfig.getFailureDetectionTimeout() <= 0) nodeConfig.setFailureDetectionTimeout(-1);
        if (nodeConfig.getMaxConnections() <= 0) nodeConfig.setMaxConnections(-1);
    }

    // 为AofConfig填充默认值
    private void fillDefaultAofValues(AofConfig aof) {
//        if (aof.isEnable()) aof.setEnable(true); // 默认启用
        if (aof.getFilename() == null) aof.setFilename("zeus.aof");
        if (aof.getPreallocateSize() <= 0) aof.setPreallocateSize(-1);
        if (aof.getFlushInterval() <= 0) aof.setFlushInterval(-1);
        if (aof.getLargeCmdThreshold() <= 0) aof.setLargeCmdThreshold(-1);
        if (aof.getBackpressureThreshold() <= 0) aof.setBackpressureThreshold(-1);
        if (aof.getQueueSize() <= 0) aof.setQueueSize(-1);
        if (aof.getMinBatchSize() <= 0) aof.setMinBatchSize(-1);
        if (aof.getMaxBatchSize() <= 0) aof.setMaxBatchSize(-1);
        if (aof.getMinBatchTimeout() <= 0) aof.setMinBatchTimeout(-1);
        if (aof.getMaxBatchTimeout() <= 0) aof.setMaxBatchTimeout(-1);
    }

    // 为ZdbConfig填充默认值
    private void fillDefaultZdbValues(ZdbConfig zdb) {
        if (!zdb.isEnable()) zdb.setEnable(false); // 默认禁用
    }

    public ZeusConfig getConfig() {
        if (zeusConfig != null) {
            return this.zeusConfig;
        }
        throw new RuntimeException("请先加载配置!");
    }

    public static void main(String[] args) {
        ConfigLoader configLoader = new ConfigLoader();
        configLoader.loadYaml();
    }



}
