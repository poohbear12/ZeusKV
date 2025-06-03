package com.cc.database.datastructure;


import com.cc.database.base.Sds;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @program: cc-simple-redis
 * @description: Redis String类型 todo 内部改造 支持int
 * @author: ccstar
 * @create: 2025-06-02  19:26
 **/
@Data
@NoArgsConstructor
public class RedisString implements RedisObject {

    private Sds sds;

    public RedisString(String value) {
        this.sds = new Sds(value);
    }

    public String getValue() {
        return this.sds.getValue();
    }

    public RedisString setValue(String value) {
        this.sds = new Sds(value);
        return this;
    }

    public static RedisString Instance() {
        return new RedisString();
    }
}
