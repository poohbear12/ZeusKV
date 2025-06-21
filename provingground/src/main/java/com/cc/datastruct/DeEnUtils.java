package com.cc.datastruct;


import com.cc.database.datastructure.ZeusList;
import com.cc.hash.ZeusSet;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

/**
 * @program: zeus-kv
 * @description: 基于kryo编解码工具类
 * @author: ccstar
 * @create: 2025-06-15  23:31
 **/


public class DeEnUtils {

  private static Kryo kryo;

  static {
    kryo = new Kryo();
    kryo.register(ZeusList.class);
    kryo.register(ZeusSet.class);
    kryo.register(ArrayList.class);
  }

  /**
   * 编码方法：将对象序列化为字节数组
   *
   * @param object 要序列化的对象
   * @param clazz  对象的类类型
   * @param <T>    对象的泛型类型
   * @return 序列化后的字节数组
   */
  public static <T> byte[] encode(T object, Class<T> clazz) {
    // 从线程本地获取 Kryo 实例

    // 使用 ByteArrayOutputStream 来存储序列化后的字节数据
    // todo 使用反射扫描实现自动注册
    try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
         Output output = new Output(byteArrayOutputStream)) {

      // 序列化对象
      kryo.writeObject(output, object);
      output.flush();

      // 返回字节数组
      return byteArrayOutputStream.toByteArray();
    } catch (Exception e) {
      throw new RuntimeException("序列化对象失败", e);
    }
  }

  /**
   * 解码方法：将字节数组反序列化为对象
   *
   * @param bytes 要反序列化的字节数组
   * @param clazz 对象的类类型
   * @param <T>   对象的泛型类型
   * @return 反序列化后的对象
   */
  public static <T> T decode(byte[] bytes, Class<T> clazz) {
    if (bytes == null || bytes.length == 0) {
      return null;
    }

    // 从线程本地获取 Kryo 实例

    // 使用 ByteArrayInputStream 来读取字节数组
    try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
         Input input = new Input(byteArrayInputStream)) {

      // 反序列化对象
      return kryo.readObject(input, clazz);
    } catch (Exception e) {
      throw new RuntimeException("反序列化对象失败", e);
    }
  }


  public static void main(String[] args) {
//    ZeusList<Integer> objectZeusList = new ZeusList<>();
//    byte[] encode = DeEnUtils.encode(objectZeusList, ZeusList.class);
//    ZeusList decode = DeEnUtils.decode(encode, ZeusList.class);
//    ZeusSet zeusSet = new ZeusSet();
//    byte[] encode1 = DeEnUtils.encode(zeusSet, ZeusSet.class);
//    ZeusSet decode1 = DeEnUtils.decode(encode1, ZeusSet.class);


  }
}
