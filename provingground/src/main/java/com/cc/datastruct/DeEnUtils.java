package com.cc.datastruct;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.LinkedList;

/**
 * @program: zeus-kv
 * @description:
 * @author: ccstar
 * @create: 2025-06-15  23:31
 **/


public class DeEnUtils {

  // 编码方法：将对象序列化为字节数组
  // 编码方法：将对象序列化为字节数组
  public static <T> byte[] encode(T object, Class<T> clazz) {
    // 创建 Kryo 实例
    Kryo kryo = new Kryo();
    // 注册要序列化的类
    kryo.register(clazz);
    kryo.register(TList.class);
    kryo.register(LinkedList.class);
    // 如果类中有特殊字段类型，也需要注册
      kryo.register(byte[].class);

    // 使用 ByteArrayOutputStream 来存储序列化后的字节数据
    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    Output output = new Output(byteArrayOutputStream);

    // 序列化对象
    kryo.writeObject(output, object);
    output.close();

    // 返回字节数组
    return byteArrayOutputStream.toByteArray();
  }

  // 解码方法：将字节数组反序列化为对象
  public static <T> T decode(byte[] bytes, Class<T> clazz) {
    // 创建 Kryo 实例
    Kryo kryo = new Kryo();
    // 注册要反序列化的类
    kryo.register(clazz);
    kryo.register(TList.class);
    kryo.register(LinkedList.class);

    // 如果类中有特殊字段类型，也需要注册
      kryo.register(byte[].class);

    // 使用 ByteArrayInputStream 来读取字节数组
    ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
    Input input = new Input(byteArrayInputStream);

    // 反序列化对象
    T object = kryo.readObject(input, clazz);
    input.close();

    // 返回反序列化后的对象
    return object;
  }

  public static void main(String[] args) {
    // 测试 KVByte 类
    KVByte kvByte = new KVByte();
    kvByte.setKey("testKey");
    kvByte.setValue(new byte[]{1, 2, 3});

    // 编码
    byte[] encodedBytes = DeEnUtils.encode(kvByte, KVByte.class);
    System.out.println("Encoded bytes length: " + encodedBytes.length);

    // 解码
    KVByte decodedKVByte = DeEnUtils.decode(encodedBytes, KVByte.class);
    System.out.println("Decoded key: " + decodedKVByte.getKey());
    System.out.println("Decoded value: " + java.util.Arrays.toString(decodedKVByte.getValue()));
  }
}
