package com.cc.hash;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

import java.io.*;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @program: zeus-kv
 * @description:
 * @author: ccstar
 * @create: 2025-06-20  07:03
 **/


public class KryoSerializer {
//    // 使用线程安全的 Kryo 池替代 ThreadLocal
//    private static final KryoPool pool;
//    // 注册 ID 起始值
//    private static final int INITIAL_REGISTRATION_ID = 100;
//    // 记录已注册的类及其 ID，使用线程安全的 ConcurrentHashMap
//    private static final ConcurrentHashMap<Class<?>, Integer> registeredClasses = new ConcurrentHashMap<>();
//    // 下一个可用的注册 ID，使用原子操作保证线程安全
//    private static int nextRegistrationId = INITIAL_REGISTRATION_ID;
//    // 包扫描路径
//    private static final String SCAN_PACKAGE = "com.cc.database";
//
//    static {
//        // 初始化 Kryo 工厂
//        KryoFactory factory = () -> {
//            Kryo kryo = new Kryo();
//            // 配置 Kryo
//            configureKryoInstance(kryo);
//            return kryo;
//        };
//
//        // 创建 Kryo 池
//        pool = new KryoPool.Builder(factory)
//                .softReferences()
//                .build();
//    }
//
//    /**
//     * 配置 Kryo 实例
//     */
//    private static void configureKryoInstance(Kryo kryo) {
//        // 设置不自动注册类，提高性能
//        kryo.setRegistrationRequired(true);
//        // 配置其他 Kryo 参数
//        kryo.setReferences(true);
//        kryo.setInstantiatorStrategy(new Kryo.DefaultInstantiatorStrategy(
//                new com.esotericsoftware.kryo.serializers.FieldSerializer.CachedFieldSerializerStrategy()
//        ));
//
//        // 注册常用类
//        registerCommonClasses(kryo);
//
//        // 自动扫描并注册指定包下的类
//        autoRegisterClasses(kryo, SCAN_PACKAGE);
//    }
//
//    /**
//     * 注册常用类
//     */
//    private static void registerCommonClasses(Kryo kryo) {
//        // 注册标准类
//        registerClass(kryo, java.util.Date.class);
//        registerClass(kryo, java.math.BigDecimal.class);
//        registerClass(kryo, java.math.BigInteger.class);
//
//        // 注册集合类
//        registerClass(kryo, java.util.ArrayList.class);
//        registerClass(kryo, java.util.HashMap.class);
//        registerClass(kryo, java.util.HashSet.class);
//        registerClass(kryo, java.util.LinkedList.class);
//        registerClass(kryo, java.util.TreeMap.class);
//        registerClass(kryo, java.util.TreeSet.class);
//
//        // 注册其他常用类
//        registerClass(kryo, java.util.UUID.class);
//        registerClass(kryo, java.lang.Exception.class);
//        registerClass(kryo, java.lang.RuntimeException.class);
//    }
//
//    /**
//     * 自动扫描并注册指定包下的类
//     */
//    private static void autoRegisterClasses(Kryo kryo, String packageName) {
//        try {
//            // 使用 Reflections 库扫描指定包下的所有类
//            Reflections reflections = new Reflections(packageName);
//            Set<Class<?>> classes = reflections.getSubTypesOf(Object.class);
//
//            // 注册扫描到的类
//            for (Class<?> clazz : classes) {
//                // 排除接口、抽象类和枚举
//                if (!clazz.isInterface() &&
//                        !Modifier.isAbstract(clazz.getModifiers()) &&
//                        !clazz.isEnum()) {
//                    registerClass(kryo, clazz);
//                }
//            }
//
//            System.out.println("自动注册了 " + classes.size() + " 个类");
//        } catch (Exception e) {
//            System.err.println("自动注册类时出错: " + e.getMessage());
//            e.printStackTrace();
//        }
//    }
//
//    /**
//     * 注册单个类
//     */
//    private synchronized static void registerClass(Kryo kryo, Class<?> clazz) {
//        if (!registeredClasses.containsKey(clazz)) {
//            kryo.register(clazz, nextRegistrationId++);
//            registeredClasses.put(clazz, nextRegistrationId - 1);
//            System.out.println("注册类: " + clazz.getName() + ", ID: " + (nextRegistrationId - 1));
//        }
//    }
//
//    /**
//     * 开放接口：允许手动注册类
//     */
//    public static void registerClass(Class<?> clazz) {
//        pool.run(kryo -> {
//            registerClass(kryo, clazz);
//            return null;
//        });
//    }
//
//    /**
//     * 开放接口：允许批量注册类
//     */
//    public static void registerClasses(Class<?>... classes) {
//        pool.run(kryo -> {
//            for (Class<?> clazz : classes) {
//                registerClass(kryo, clazz);
//            }
//            return null;
//        });
//    }
//
//    /**
//     * 开放接口：获取已注册的类
//     */
//    public static Map<Class<?>, Integer> getRegisteredClasses() {
//        return new HashMap<>(registeredClasses);
//    }
//
//    /**
//     * 编码方法：将对象序列化为字节数组
//     */
//    public static <T> byte[] encode(T object, Class<T> clazz) {
//        if (object == null) {
//            return new byte[0];
//        }
//
//        // 从池中获取 Kryo 实例
//        return pool.run(kryo -> {
//            // 确保类已注册
//            ensureClassRegistered(kryo, clazz);
//
//            try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
//                 Output output = new Output(baos, 4096)) { // 初始缓冲区大小 4KB
//
//                // 序列化对象
//                kryo.writeObject(output, object);
//                output.flush();
//
//                // 返回字节数组
//                return baos.toByteArray();
//            } catch (Exception e) {
//                throw new RuntimeException("序列化对象失败: " + clazz.getName(), e);
//            }
//        });
//    }
//
//    /**
//     * 解码方法：将字节数组反序列化为对象
//     */
//    public static <T> T decode(byte[] bytes, Class<T> clazz) {
//        if (bytes == null || bytes.length == 0) {
//            return null;
//        }
//
//        // 从池中获取 Kryo 实例
//        return pool.run(kryo -> {
//            // 确保类已注册
//            ensureClassRegistered(kryo, clazz);
//
//            try (ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
//                 Input input = new Input(bais)) {
//
//                // 反序列化对象
//                return kryo.readObject(input, clazz);
//            } catch (Exception e) {
//                throw new RuntimeException("反序列化对象失败: " + clazz.getName(), e);
//            }
//        });
//    }
//
//    /**
//     * 确保类已注册
//     */
//    private static void ensureClassRegistered(Kryo kryo, Class<?> clazz) {
//        if (!registeredClasses.containsKey(clazz)) {
//            synchronized (KryoSerializer.class) {
//                if (!registeredClasses.containsKey(clazz)) {
//                    registerClass(kryo, clazz);
//                }
//            }
//        }
//    }
//
//    /**
//     * 编码集合对象
//     */
//    public static <T> byte[] encodeCollection(java.util.Collection<T> collection, Class<T> elementClass) {
//        return pool.run(kryo -> {
//            try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
//                 Output output = new Output(baos)) {
//
//                // 写入集合大小
//                output.writeInt(collection.size(), true);
//
//                // 写入每个元素
//                for (T element : collection) {
//                    kryo.writeObject(output, element);
//                }
//
//                output.flush();
//                return baos.toByteArray();
//            } catch (Exception e) {
//                throw new RuntimeException("序列化集合失败: " + elementClass.getName(), e);
//            }
//        });
//    }
//
//    /**
//     * 解码集合对象
//     */
//    public static <T> java.util.List<T> decodeList(byte[] bytes, Class<T> elementClass) {
//        if (bytes == null || bytes.length == 0) {
//            return java.util.Collections.emptyList();
//        }
//
//        return pool.run(kryo -> {
//            try (ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
//                 Input input = new Input(bais)) {
//
//                // 读取集合大小
//                int size = input.readInt(true);
//                java.util.List<T> list = new java.util.ArrayList<>(size);
//
//                // 读取每个元素
//                for (int i = 0; i < size; i++) {
//                    list.add(kryo.readObject(input, elementClass));
//                }
//
//                return list;
//            } catch (Exception e) {
//                throw new RuntimeException("反序列化集合失败: " + elementClass.getName(), e);
//            }
//        });
//    }
}
