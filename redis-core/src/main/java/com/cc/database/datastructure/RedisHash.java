package com.cc.database.datastructure;

import java.util.HashMap;

/**
 * @program: cc-simple-redis
 * @description:
 * @author: ccstar
 * @create: 2025-06-03  15:09
 **/


public class RedisHash<T> implements RedisObject{

    private final HashMap<RedisString, T> hashMap = new HashMap<>();

    public void set() {
    }

}
