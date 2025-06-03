package com.cc.database.datastructure;

import java.util.HashSet;
import java.util.Set;

/**
 * @program: cc-simple-redis
 * @description: Set
 * @author: ccstar
 * @create: 2025-06-03  15:09
 **/

public class RedisSet<T> implements RedisObject{
    // todo 暂时
    private final Set<T> set = new HashSet<>();

    public void set(T value) {
        set.add(value);
    }

    public void del(T value) {
        set.remove(value);
    }

}
