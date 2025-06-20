package com.cc.database.datastructure;

import com.cc.hash.ZeusData;
import java.util.LinkedList;
import java.util.List;

/**
 * @program: zeus-kv
 * @description: List 双向列表
 * @author: ccstar
 * @create: 2025-06-18  23:55
 **/

public class ZeusList<T> implements ZeusData {
  private final List<T> innrList = new LinkedList<>();

  public void lpush(T t) {
    innrList.addFirst(t);
  }

  public T lpop() {
    return innrList.removeFirst();
  }

  public void rpush(T t) {
    innrList.addLast(t);
  }

  public T rpop() {
    return innrList.removeLast();
  }

  public int size() {
    return innrList.size();
  }


}
