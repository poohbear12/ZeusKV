package com.cc.database.core;


import com.cc.database.datastructure.RedisObject;
import com.cc.database.datastructure.RedisString;
import lombok.Data;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @program: cc-simple-redis
 * @description: 数据库管理核心类
 * @author: ccstar
 * @create: 2025-06-01  16:09
 **/
@Data
public class RedisCoreImpl implements RedisCore {

    /**
     * 数据库集合
     */
    private final List<RedisDb> databases;

    /**
     * 数据库个数
     */
    private int dbNum;

    /**
     * 当前指定数据库
     */
    private int currentDbIndex = 0;

    /**
     * 默认构造
     */
    public RedisCoreImpl(){
        this(16);
    }

    /**
     * 传入num创建RedisDb
     * @param num
     */
    public RedisCoreImpl(int num) {
        this.databases = new ArrayList<>();
        for (int i = 0; i < num; i++) {
            databases.add(new RedisDb());
        }
    }

    @Override
    public int size() {
        return this.dbNum;
    }

    @Override
    public void selectDb(int idx) {
        this.currentDbIndex = idx;
    }

    @Override
    public int currentDb() {
        return currentDbIndex;
    }

    /**
     * todo 待优化
     * @param key
     * @param value
     */
    @Override
    public void put(RedisString key, RedisObject value) {
        RedisDb redisDb = databases.get(currentDbIndex);
        redisDb.put(key,value);
    }

    /**
     * todo 待优化
     * @param key
     * @return
     */
    @Override
    public RedisObject get(RedisString key) {
        RedisDb redisDb = databases.get(currentDbIndex);
        return redisDb.get(key);
    }

    @Override
    public Set<RedisString> keys() {
        return null;
    }


    @Override
    public void flushAll() {

    }

    @Override
    public void flush() {

    }
}
