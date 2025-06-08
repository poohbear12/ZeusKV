package com.cc.database.datastructure;


import lombok.Data;
import lombok.NoArgsConstructor;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

/**
 * @program: cc-simple-redis
 * @description: Redis String类型 todo 内部改造 支持int
 * @author: ccstar
 * @create: 2025-06-02  19:26
 **/
@NoArgsConstructor
public class RedisString implements RedisObject {

    private byte[] buf;

    public RedisString(String value) {
        this.buf = value.getBytes(StandardCharsets.UTF_8);
    }

    public String getValue() {
        return new String(buf,StandardCharsets.UTF_8);
    }

    public RedisString setValue(String value) {
        this.buf = value.getBytes(StandardCharsets.UTF_8);
        return this;
    }

    public static RedisString Instance() {
        return new RedisString();
    }

    /**
     * 重写equals
     * @param o
     * @return
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true; // 如果是同一个对象，直接返回true
        if (o == null || getClass() != o.getClass()) return false; // 如果对象为null或者不是同一个类，返回false
        RedisString that = (RedisString) o; // 类型转换
        return Arrays.equals(buf, that.buf); // 比较byte数组是否相等
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(buf); // 使用Arrays的hashCode方法计算byte数组的哈希值
    }
}
