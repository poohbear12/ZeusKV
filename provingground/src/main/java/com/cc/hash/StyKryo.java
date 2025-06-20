package com.cc.hash;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.util.Pool;
import org.objenesis.strategy.StdInstantiatorStrategy;
import org.reflections.Reflections;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.reflections.scanners.SubTypesScanner;

public class StyKryo {
    // 注册 ID 起始值
    private static final int INITIAL_REGISTRATION_ID = 100;
    // 记录已注册的类及其 ID
    private static final ConcurrentHashMap<Class<?>, Integer> registeredClasses = new ConcurrentHashMap<>();
    // 下一个可用的注册 ID
    private static int nextRegistrationId = INITIAL_REGISTRATION_ID;
    // 包扫描路径
    private static final String SCAN_PACKAGE = "com.cc.database.datastructure";

    // Kryo 池：线程安全地管理 Kryo 实例
    private static final Pool<Kryo> kryoPool = new Pool<Kryo>(true, false, 8) {
        @Override
        protected Kryo create() {
            Kryo kryo = new Kryo();
            configureKryoInstance(kryo);
            return kryo;
        }
    };

    // Output 池
    private static final Pool<Output> outputPool = new Pool<Output>(true, false, 16) {
        @Override
        protected Output create() {
            return new Output(4096, -1); // 初始缓冲区 4KB，无限制增长
        }

        @Override
        protected void reset(Output output) {
            output.reset(); // 重置 Output 以便复用
        }
    };

    // Input 池
    private static final Pool<Input> inputPool = new Pool<Input>(true, false, 16) {
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
     * 配置 Kryo 实例
     */
    private static void configureKryoInstance(Kryo kryo) {
        // 设置不自动注册类，提高性能
        kryo.setRegistrationRequired(true);
        // 配置其他 Kryo 参数
        kryo.setReferences(true);

        // 修复：Kryo 5.6+ 的 InstantiatorStrategy 配置方式
        kryo.setInstantiatorStrategy(new StdInstantiatorStrategy());

        // 注册常用类
        registerCommonClasses(kryo);

        // 自动扫描并注册指定包下的类
        autoRegisterClasses(kryo, SCAN_PACKAGE);
    }

    /**
     * 注册常用类以提高性能
     */
    private static void registerCommonClasses(Kryo kryo) {
//        // 注册标准类
//        registerClass(kryo, java.util.Date.class);
//        registerClass(kryo, java.math.BigDecimal.class);
//        registerClass(kryo, java.math.BigInteger.class);
//
        // 注册集合类
        registerClass(kryo, java.util.ArrayList.class);
        registerClass(kryo, java.util.HashMap.class);
        registerClass(kryo, java.util.HashSet.class);
        registerClass(kryo, java.util.LinkedList.class);
        registerClass(kryo, java.util.TreeMap.class);
        registerClass(kryo, java.util.TreeSet.class);
    }

    /**
     * 自动扫描并注册指定包下的类
     */
    private static void autoRegisterClasses(Kryo kryo, String packageName) {
        try {
            // 使用 Reflections 库扫描指定包下的所有类
            Reflections reflections = new Reflections(packageName, new SubTypesScanner(false));
            Set<Class<?>> classes = reflections.getSubTypesOf(Object.class);

            // 注册扫描到的类
            for (Class<?> clazz : classes) {
                // 排除接口、抽象类和枚举
                if (!clazz.isInterface() &&
                        !Modifier.isAbstract(clazz.getModifiers()) &&
                        !clazz.isEnum()) {
                    registerClass(kryo, clazz);
                }
            }

            System.out.println("自动注册了 " + classes.size() + " 个类");
        } catch (Exception e) {
            System.err.println("自动注册类时出错: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 注册单个类
     */
    private synchronized static void registerClass(Kryo kryo, Class<?> clazz) {
        if (!registeredClasses.containsKey(clazz)) {
            kryo.register(clazz, nextRegistrationId++);
            registeredClasses.put(clazz, nextRegistrationId - 1);
            System.out.println("注册类: " + clazz.getName() + ", ID: " + (nextRegistrationId - 1));
        }
    }



    /**
     * 开放接口：获取已注册的类
     */
    public static Map<Class<?>, Integer> getRegisteredClasses() {
        return new HashMap<>(registeredClasses);
    }
    /**
     * 开放接口：允许手动注册类
     */
    public static void registerClass(Class<?> clazz) {
        Kryo kryo = kryoPool.obtain();
        try {
            registerClass(kryo, clazz);
        } finally {
            kryoPool.free(kryo);
        }
    }

    /**
     * 开放接口：允许批量注册类
     */
    public static void registerClasses(Class<?>... classes) {
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
     * 确保类已注册
     */
    private static void ensureClassRegistered(Kryo kryo, Class<?> clazz) {
        if (!registeredClasses.containsKey(clazz)) {
            synchronized (KryoSerializer.class) {
                if (!registeredClasses.containsKey(clazz)) {
                    registerClass(kryo, clazz);
                }
            }
        }
    }

    /**
     * 编码方法：将对象序列化为字节数组
     */
    public static <T> byte[] encode(T object, Class<T> clazz) {
        if (object == null) {
            return new byte[0];
        }

        Kryo kryo = kryoPool.obtain();
        Output output = outputPool.obtain();
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            output.setOutputStream(baos);

            // 确保类已注册
            ensureClassRegistered(kryo, clazz);

            // 序列化对象
            kryo.writeObject(output, object);
            output.flush();

            // 返回字节数组
            return baos.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("序列化对象失败: " + clazz.getName(), e);
        } finally {
            outputPool.free(output);
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

            // 确保类已注册
            ensureClassRegistered(kryo, clazz);

            // 反序列化对象
            return kryo.readObject(input, clazz);
        } catch (Exception e) {
            throw new RuntimeException("反序列化对象失败: " + clazz.getName(), e);
        } finally {
            inputPool.free(input);
            kryoPool.free(kryo);
        }
    }

    public static void main(String[] args) {
        ZeusList<Long> integerZeusList = new ZeusList<>();
        integerZeusList.lpush(0L);
        integerZeusList.lpush(0L);
        integerZeusList.lpush(0L);
        integerZeusList.lpush(0L);
        integerZeusList.lpush(0L);
        byte[] encode = StyKryo.encode(integerZeusList, ZeusList.class);
        ZeusList<Long> decode = StyKryo.decode(encode, ZeusList.class);
        System.out.println(decode.lpop());
        System.out.println(decode.lpop());
        System.out.println(decode.lpop());
        System.out.println(decode.lpop());
    }
}
