package com.cc.database.base;


import com.cc.database.datastructure.RedisString;

import java.util.HashMap;
import java.util.Map;

/**
 * @program: cc-simple-redis
 * @description: 哈希表
 * @author: ccstar
 * @create: 2025-06-02  17:54
 **/

public class Dict<K,V> {

    // todo 目前先利用hashmap代替测试,后续实现原有功能
    Map<K,V> map = new HashMap<>();

    public void put(K key, V value) {
        map.put(key,value);
    }

    public V get(K key) {
        return map.get(key);
    }

    public boolean exists(String key) {
        return map.containsKey(new RedisString(key));
    }

    public int size() {
        return map.size();
    }
}
