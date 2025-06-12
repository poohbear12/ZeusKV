package com.cc.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * @program: zeus-kv
 * @description: zeus server 启动器
 * @author: ccstar
 * @create: 2025-06-09  21:05
 **/
public class BannerPrinter {

    private static final Logger log = LoggerFactory.getLogger(BannerPrinter.class);
    private static final String BANNER_PATH = "banner.txt"; // 类路径下的文件路径

    /**
     * 读取并打印 banner.txt 内容（类路径下）
     * @throws IOException 文件读取异常
     */
    public static void printBannerFromFile() {
        try (InputStream is = BannerPrinter.class.getClassLoader().getResourceAsStream(BANNER_PATH);
             BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
            
            if (is == null) {
                log.error("未找到 banner.txt 文件，请确保文件位于类路径下");
                return;
            }
            String line;
            while ((line = reader.readLine()) != null) {
                log.info(line); // 使用日志打印每一行
            }
            
        } catch (IOException e) {
            log.error("读取 banner.txt 失败: {}", e.getMessage());
        }
    }
    // 示例用法
    public static void main(String[] args) {
        printBannerFromFile(); // 调用方法打印 banner
    }
}