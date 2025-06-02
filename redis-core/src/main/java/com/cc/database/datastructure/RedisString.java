package com.cc.database.datastructure;


import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @program: cc-simple-redis
 * @description: Redis String类型
 * @author: ccstar
 * @create: 2025-06-02  19:26
 **/
@Data
@AllArgsConstructor
public class RedisString implements RedisObject {
    private String value;

}
