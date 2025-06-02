package com.cc.database.core;


import com.cc.database.base.Dict;
import com.cc.database.datastructure.RedisObject;
import com.cc.database.datastructure.RedisString;

/**
 * @program: cc-simple-redis
 * @description: RedisDb  todo 待优化
 * @author: ccstar
 * @create: 2025-06-02  17:54
 **/
public class RedisDb {

    /**
     * 字典
     */
    private final Dict<RedisString, RedisObject> dict;

    public RedisDb() {
        this.dict = new Dict();
    }

    public void put(RedisString key, RedisObject value) {
        dict.put(key,value);
    }

    public RedisObject get(RedisString key) {
        return dict.get(key);
    }
}
