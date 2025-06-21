package com.cc.hash1;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.util.DefaultInstantiatorStrategy;
import com.esotericsoftware.kryo.util.Pool;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.lang.reflect.Modifier;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import lombok.extern.slf4j.Slf4j;
import org.objenesis.strategy.StdInstantiatorStrategy;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;

/**
 * @program: zeus-kv
 * @description: Kryo序列化工具类
 * @author: ccstar
 * @create: 2025-06-20  09:07
 **/

@Slf4j
public class KryoSerialize {

  /**
   * 注册ID初始值
   */
  private static final int INITIAL_REGISTRATION_ID = 23;

  /**
   * 记录已注册类
   */
  private static final ConcurrentHashMap<Class<?>, Integer> registeredClasses = new ConcurrentHashMap<>();

  /**
   * 包扫描路径
   */
  private static final String SCAN_PACKAGE = "com.cc.database.datastructure";
  /**
   * outputPool 输出池 优化内存使用
   */
  private static final Pool<Output> outputPool = new Pool<Output>(true, false, 32) {
    @Override
    protected Output create() {
      return new Output(4096, -1); // 初始缓冲区 4KB，无限制增长
    }

    @Override
    protected void reset(Output output) {
      output.reset();
    }
  };
  /**
   * inputPool 输出池 优化内存使用
   */
  private static final Pool<Input> inputPool = new Pool<Input>(true, false, 32) {
    @Override
    protected Input create() {
      return new Input(4096); // 初始缓冲区 4KB
    }

    @Override
    protected void reset(Input input) {
      input.reset(); // 重置 Input 以便复用
    }
  };
  /**
   * 下一个注册ID
   */
  private static int nextRegistrationId = INITIAL_REGISTRATION_ID;
  /**
   * KryoPool 对象池
   */
  private static final Pool<Kryo> kryoPool = new Pool<Kryo>(true, false, 32) {
    @Override
    protected Kryo create() {
      Kryo kryo = new Kryo();
      configureKryoInstance(kryo);
      return kryo;
    }
  };

  /**
   * 配置 Kryo 实例
   */
  private static void configureKryoInstance(Kryo kryo) {
    // 设置不自动注册类，提高性能
    kryo.setRegistrationRequired(true);

    // 记录引用关系
    kryo.setReferences(true);

    // 设置顶层策略为 DefaultInstantiatorStrategy 默认无参构造
    // 嵌套 StdInstantiatorStrategy 作为后备，直接通过 JVM 原生方法创建对象（如 sun.misc.Unsafe 或 java.lang.reflect.Constructor）
    // 不调用任何构造函数，即使对象没有无参构造函数也能实例化。
    kryo.setInstantiatorStrategy(new DefaultInstantiatorStrategy(
        new StdInstantiatorStrategy()
    ));

    // 注册常用类
    registerCommonClasses(kryo);

    // 自动扫描并注册指定包下的类
    autoRegisterClasses(kryo, SCAN_PACKAGE);

    // 额外注册Java标准库中的常见内部类
    registerJdkInternalClasses(kryo);
  }

  /**
   * 注册常用类以提高性能
   */
  private static void registerCommonClasses(Kryo kryo) {
    // 注册集合类
    registerClass(kryo, java.util.List.class);
    registerClass(kryo, java.util.Map.class);
    registerClass(kryo, java.util.Set.class);
    registerClass(kryo, java.util.ArrayList.class);
    registerClass(kryo, java.util.Arrays.class);
    registerClass(kryo, java.util.LinkedList.class);
    registerClass(kryo, java.util.HashMap.class);
    registerClass(kryo, java.util.HashSet.class);
    registerClass(kryo, java.util.TreeMap.class);
    registerClass(kryo, java.util.TreeSet.class);
  }

  /**
   * 自动扫描并注册指定包下的类
   */
  private static void autoRegisterClasses(Kryo kryo, String packageName) {
    try {
      // 扫描指定包下的所有类，递归
      Reflections reflections = new Reflections(packageName, new SubTypesScanner(false));
      Set<Class<?>> classes = reflections.getSubTypesOf(Object.class);
      log.debug("扫描到 {} 个类，开始注册...", classes.size());
      for (Class<?> clazz : classes) {
        // 放宽类注册条件，允许注册内部类
        if (canRegisterClass(clazz)) {
          registerClass(kryo, clazz);

          // 额外注册该类的所有内部类
          registerInnerClasses(kryo, clazz);
        }
      }
      log.debug("自动注册完成：共注册 {} 个类", registeredClasses.size());
    } catch (Exception e) {
      log.error("自动注册类时出错: {}", e.getMessage(), e);
    }
  }

  /**
   * 判断类是否可以注册（允许内部类注册）
   */
  private static boolean canRegisterClass(Class<?> clazz) {
    // 排除接口、抽象类，但允许内部类
    return !clazz.isInterface() &&
        !Modifier.isAbstract(clazz.getModifiers()) &&
        !clazz.isEnum();
  }

  /**
   * 递归注册类的所有内部类
   */
  private static void registerInnerClasses(Kryo kryo, Class<?> clazz) {
    // 获取所有内部类
    Class<?>[] innerClasses = clazz.getDeclaredClasses();
    for (Class<?> innerClass : innerClasses) {
      // 排除接口、抽象类和枚举
      if (canRegisterClass(innerClass)) {
        registerClass(kryo, innerClass);
        // 递归注册更深层次的内部类
        registerInnerClasses(kryo, innerClass);
      }
    }
  }

  /**
   * 注册内部类
   *
   * @param kryo
   */
  private static void registerJdkInternalClasses(Kryo kryo) {
    try {
      // 注册Arrays的内部ArrayList类
      Class<?> arrayListClass = Class.forName("java.util.Arrays$ArrayList");
      registerClass(kryo, arrayListClass);

      // 注册其他可能需要的JDK内部类
      Class<?>[] jdkInternalClasses = {
          Class.forName("java.util.Collections$EmptyList"),
          Class.forName("java.util.Collections$SingletonList"),
          // 可根据需要添加更多
      };

      for (Class<?> clazz : jdkInternalClasses) {
        registerClass(kryo, clazz);
      }
    } catch (Exception e) {
      log.warn("注册JDK内部类时出错", e);
    }
  }

  /**
   * 注册单个类
   */
  private static void registerClass(Kryo kryo, Class<?> clazz) {
    if (!registeredClasses.contains(clazz)) {
      kryo.register(clazz, nextRegistrationId++);
      registeredClasses.put(clazz, nextRegistrationId - 1);
      log.debug(STR."注册类: \{clazz.getName()}, ID: \{nextRegistrationId - 1}");
    }
  }

  /**
   * 私有接口：允许手动注册类
   */
  private static void registerClass(Class<?> clazz) {
    Kryo kryo = kryoPool.obtain();
    try {
      registerClass(kryo, clazz);
    } finally {
      kryoPool.free(kryo);
    }
  }

  /**
   * 私有接口：允许批量注册类
   */
  private static void registerClasses(Class<?>... classes) {
    Kryo kryo = kryoPool.obtain();
    try {
      for (Class<?> clazz : classes) {
        registerClass(kryo, clazz);
      }
    } finally {
      kryoPool.free(kryo);
    }
  }

  /**
   * 判断类是否被注册
   */
  private static void ensureClassRegistered(Kryo kryo, Class<?> clazz) {
    if (!registeredClasses.containsKey(clazz)) {
      throw new RuntimeException("Kryo异常，类未注册: " + clazz.getName());
    }
  }

  /**
   * 编码方法：将对象序列化为字节数组
   * 1. clazz == Object
   * 2. clazz == Interface
   * 3. clazz == other throw new
   */
  /**
   * 编码方法：将对象序列化为字节数组
   */
  public static <T> byte[] encode(T object, Class<T> clazz) {
    if (object == null) {
      return new byte[0];
    }

    // 先判断是否为具体类（非接口、非抽象、非枚举等）
    if (isConcreteClass(clazz)) {
      return handleConcreteClassSerialization(object, clazz);
    }
    // 再判断是否为接口
    else if (clazz.isInterface()) {
      return handleInterfaceSerialization(object, clazz);
    }
    // 最后判断是否为抽象类
    else if (Modifier.isAbstract(clazz.getModifiers())) {
      return handleAbstractClassSerialization(object, clazz);
    }
    // 不支持的类型
    else {
      throw new RuntimeException(STR."不支持的类类型: \{clazz.getName()}");
    }
  }

  /**
   * 处理具体类的序列化
   */
  private static <T> byte[] handleConcreteClassSerialization(T object, Class<T> clazz) {
    Kryo kryo = kryoPool.obtain();
    Output output = outputPool.obtain();
    try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
      output.setOutputStream(baos);
      ensureClassRegistered(kryo, clazz);
      kryo.writeObject(output, object);
      output.flush();
      return baos.toByteArray();
    } catch (Exception e) {
      throw new RuntimeException(STR."Kryo > 序列化具体类失败: \{clazz.getName()}", e);
    } finally {
      outputPool.free(output);
      kryoPool.free(kryo);
    }
  }

  /**
   * 处理接口的序列化
   */
  private static <T> byte[] handleInterfaceSerialization(T object, Class<T> interfaceClass) {
    // 确保对象实现了该接口
    if (!interfaceClass.isAssignableFrom(object.getClass())) {
      throw new RuntimeException(STR."对象 \{object.getClass().getName()} 未实现接口 \{interfaceClass.getName()}");
    }

    // 获取对象的实际实现类
    Class<?> implClass = object.getClass();

    // 检查Kryo是否已注册该实现类
    boolean isImplClassRegistered = isClassRegistered(implClass);

    // 如果未注册，尝试注册该实现类
    if (!isImplClassRegistered) {
      log.info("注册接口 {} 的实现类 {}", interfaceClass.getName(), implClass.getName());
      registerClass(implClass);
    }

    // 序列化对象（包含实现类信息）
    Kryo kryo = kryoPool.obtain();
    Output output = outputPool.obtain();
    try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
      output.setOutputStream(baos);

      // 写入实现类名称
      output.writeString(implClass.getName());

      // 序列化对象
      kryo.writeObject(output, object);
      output.flush();

      return baos.toByteArray();
    } catch (Exception e) {
      throw new RuntimeException(STR."序列化接口 \{interfaceClass.getName()} 失败", e);
    } finally {
      outputPool.free(output);
      kryoPool.free(kryo);
    }
  }

  /**
   * 处理抽象类的序列化
   */
  private static <T> byte[] handleAbstractClassSerialization(T object, Class<T> abstractClass) {
    // 确保对象是抽象类的具体子类
    if (!abstractClass.isInstance(object)) {
      throw new RuntimeException("对象 " + object.getClass().getName() +
          " 不是抽象类 " + abstractClass.getName() + " 的子类");
    }

    // 获取对象的实际具体类
    Class<?> concreteClass = object.getClass();

    // 检查并注册具体类
    if (!isClassRegistered(concreteClass)) {
      log.info("注册抽象类 {} 的具体子类 {}", abstractClass.getName(), concreteClass.getName());
      registerClass(concreteClass);
    }

    // 序列化对象（包含具体类信息）
    Kryo kryo = kryoPool.obtain();
    Output output = outputPool.obtain();
    try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
      output.setOutputStream(baos);

      // 写入具体类名称
      output.writeString(concreteClass.getName());

      // 序列化对象
      kryo.writeObject(output, object);
      output.flush();

      return baos.toByteArray();
    } catch (Exception e) {
      throw new RuntimeException("序列化抽象类 " + abstractClass.getName() + " 失败", e);
    } finally {
      outputPool.free(output);
      kryoPool.free(kryo);
    }
  }

  /**
   * 判断是否为具体类（非接口、非抽象、非枚举、非数组、非基本类型）
   */
  private static boolean isConcreteClass(Class<?> clazz) {
    return !clazz.isInterface() &&
        !Modifier.isAbstract(clazz.getModifiers()) &&
        !clazz.isEnum() &&
        !clazz.isArray() &&
        !clazz.isPrimitive();
  }

  /**
   * 检查类是否已在Kryo中注册
   */
  private static boolean isClassRegistered(Class<?> clazz) {
    Kryo kryo = kryoPool.obtain();
    try {
      // 尝试获取类的注册信息
      try {
        kryo.getRegistration(clazz);
        return true;
      } catch (IllegalArgumentException e) {
        // 类未注册
        return false;
      }
    } finally {
      kryoPool.free(kryo);
    }
  }


  /**
   * 解码方法：将字节数组反序列化为对象
   */
  public static <T> T decode(byte[] bytes, Class<T> clazz) {
    if (bytes == null || bytes.length == 0) {
      return null;
    }

    Kryo kryo = kryoPool.obtain();
    Input input = inputPool.obtain();
    try (ByteArrayInputStream bais = new ByteArrayInputStream(bytes)) {
      input.setInputStream(bais);

      // 处理具体类
      if (isConcreteClass(clazz)) {
        ensureClassRegistered(kryo, clazz);
        return kryo.readObject(input, clazz);
      }
      // 处理接口
      else if (clazz.isInterface()) {
        return handleInterfaceDeserialization(input, kryo, clazz);
      }
      // 处理抽象类
      else if (Modifier.isAbstract(clazz.getModifiers())) {
        return handleAbstractClassDeserialization(input, kryo, clazz);
      }
      // 不支持的类型
      else {
        throw new RuntimeException(STR."Kryo异常，不支持的类类型: \{clazz.getName()}");
      }
    } catch (Exception e) {
      throw new RuntimeException(STR."Kryo异常，反序列化对象失败: \{clazz.getName()}", e);
    } finally {
      inputPool.free(input);
      kryoPool.free(kryo);
    }
  }

  /**
   * 处理接口的反序列化
   */
  private static <T> T handleInterfaceDeserialization(Input input, Kryo kryo, Class<T> interfaceClass) throws Exception {
    // 读取实现类名称
    String implClassName = input.readString();
    Class<?> implClass = Class.forName(implClassName);

    // 确保实现类是接口的实现
    if (!interfaceClass.isAssignableFrom(implClass)) {
      throw new RuntimeException(STR."\{implClassName} 不是 \{interfaceClass.getName()} 的实现");
    }

    // 确保实现类已注册
    ensureClassRegistered(kryo, implClass);

    // 反序列化对象
    return (T) kryo.readObject(input, implClass);
  }

  /**
   * 处理抽象类的反序列化
   */
  private static <T> T handleAbstractClassDeserialization(Input input, Kryo kryo, Class<T> abstractClass) throws Exception {
    // 读取具体子类名称
    String subclassName = input.readString();
    Class<?> subclass = Class.forName(subclassName);

    // 确保子类是抽象类的子类
    if (!abstractClass.isAssignableFrom(subclass)) {
      throw new RuntimeException(STR."\{subclassName} 不是 \{abstractClass.getName()} 的子类");
    }

    // 确保子类已注册
    ensureClassRegistered(kryo, subclass);

    // 反序列化对象
    return (T) kryo.readObject(input, subclass);
  }

}
