package com.github.tonydeng.tcp4j.utils;

import java.lang.reflect.Method;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by tonydeng on 15/9/28.
 */
public class ThriftClientUtils {

    private static ConcurrentMap<Class<?>, Set<String>> interfaceMethodCache = new ConcurrentHashMap<>();

    private static final Random RANDOM = new Random();

    private ThriftClientUtils() {
        throw new UnsupportedOperationException();
    }

    /**
     * 获得下一个随机数
     *
     * @return
     */
    public static final int randomNextInt() {
        return RANDOM.nextInt();
    }

    /**
     * 获得接口方法名称
     *
     * @param ifaceClass
     * @return
     */
    public static final Set<String> getInterfaceMethodNames(Class<?> ifaceClass) {
        return interfaceMethodCache.computeIfAbsent(
                ifaceClass,
                i -> Stream.of(i.getInterfaces())
                        .flatMap(
                                c -> Stream.of(c.getMethods()))
                        .map(Method::getName)
                        .collect(Collectors.toSet())
        );
    }
}
