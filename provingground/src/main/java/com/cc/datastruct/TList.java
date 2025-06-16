package com.cc.datastruct;

import java.util.LinkedList;
import java.util.List;

/**
 * @program: zeus-kv
 * @description: TList 用于做测试Value
 * @author: ccstar
 * @create: 2025-06-15  23:21
 **/

public class TList<T> {

  private final List<T> list = new LinkedList<T>();

  public void lpush(T t) {
    list.addFirst(t);
  }

  public T lpop() {
    if (list.isEmpty()) {
      throw new RuntimeException("list为空！");
    }
    return list.removeFirst();
  }

  public int size() {
    return list.size();
  }
}
