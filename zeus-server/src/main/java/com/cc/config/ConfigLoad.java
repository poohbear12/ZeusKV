package com.cc.config;

/**
 * @program: zeus-kv
 * @description: 配置类加载器
 * @author: ccstar
 * @create: 2025-06-09  23:48
 **/

public class ConfigLoad {

    private ZeusConfig zeusConfig;

    public ConfigLoad() {
        this.zeusConfig = null;
    }

    public ConfigLoad(String fileName) {

    }

    /**
     * 默认加载
     */
    public void loadYaml() {
        this.zeusConfig = null;
    }

    /**
     *  指定文件名加载
     * @param FileName
     */
    public void loadYaml(String FileName) {

    }

}
