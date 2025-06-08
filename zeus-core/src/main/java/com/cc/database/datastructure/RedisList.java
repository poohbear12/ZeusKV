package com.cc.database.datastructure;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * @program: zeus-kv
 * @description: Redis list 双向链表 left < - > first
 * @author: ccstar
 * @create: 2025-06-03  15:09
 **/

public class RedisList<T> implements RedisObject{

    private final LinkedList<T> list = new LinkedList<>();

    public void lPush(T... values) {
        for (T val : values) {
            list.addFirst(val);
        }
    }

    public void lPush(List<T> values) {
        for (T val : values) {
            list.addFirst(val);
        }
    }

    public T lPop() {
        return list.removeFirst();
    }

    public void rPush(T... values) {
        for (T val : values) {
            list.addLast(val);
        }
    }

    public void rPush(List<T> values) {
        for (T val : values) {
            list.addLast(val);
        }
    }

    public T rPop() {
        return list.getLast();
    }

    public List<T> lrange(int start, int stop) {
        if (stop > list.size() || start > stop) {
            return List.of();
        }
        return list.subList(start, stop + 1);
    }

    public static RedisList Instance() {
        return new RedisList();
    }

}
