package com.cc.database.base;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @program: zeus-kv
 * @description: 动态字符串
 * @author: ccstar
 * @create: 2025-06-03  14:59
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Sds {
  // todo 后续可以改成bytes
  private String value;
}
