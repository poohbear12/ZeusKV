package com.cc.server.zeus;

import io.netty.channel.Channel;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @program: zeus-kv
 * @description: 管理Redis连接channel
 * @author: ccstar
 * @create: 2025-06-01  10:29
 **/
public class ChannelHolder {

    /**
     * channel池
     */
    private static final ConcurrentHashMap<String,Channel> channelCache = new ConcurrentHashMap<>();

    /**
     * 获取指定key 连接
     * @param key
     * @return
     */
    public static Channel  getChannel(String key){
        if (key == null) {
            throw new NullPointerException("key is not null!");
        }
        return channelCache.get(key);
    }

    /**
     * 保存channel连接
     * @param key
     * @param channel
     */
    public static void setChannel(String key, Channel channel) {
        if (key == null || channel == null) {
            throw new NullPointerException("key or channelHolder is not null!");
        }
        channelCache.put(key,channel);
    }

    /**
     * 移除key连接
     * @param key
     */
    public static void removeChannel(String key) {
        if (key == null) {
            throw new NullPointerException("key is not null!");
        }
        channelCache.remove(key);
    }

}
