package com.fr.swift.frrpc;

import com.fr.swift.exception.ProxyRegisterException;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This class created on 2018/5/29
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
public class FRProxyCache {

    private final Map<Class, Object> proxyMap;
    private final Map<Class, Object> instanceMap;


    private FRProxyCache() {
        this.proxyMap = new ConcurrentHashMap<Class, Object>();
        this.instanceMap = new ConcurrentHashMap<Class, Object>();
    }

    private static final FRProxyCache INSTANCE = new FRProxyCache();

    public static FRProxyCache getInstance() {
        return INSTANCE;
    }

    public static void registerProxy(Class classType, Object object) {
        INSTANCE.proxyMap.put(classType, object);
    }

    public static Object getProxy(Class classType) throws ProxyRegisterException {
        if (INSTANCE.proxyMap.containsKey(classType)) {
            return INSTANCE.proxyMap.get(classType);
        } else {
            throw new ProxyRegisterException(classType.getName() + "'s proxy hasn't been registed!");
        }
    }

    /**
     * @param classType -- 注册对象的接口，不能是实现类，否则动态代理无法生效
     * @param object
     */
    public static void registerInstance(Class classType, Object object) {
        INSTANCE.instanceMap.put(classType, object);
    }

    public static Object getInstance(Class classType) throws ProxyRegisterException {
        if (INSTANCE.instanceMap.containsKey(classType)) {
            return INSTANCE.instanceMap.get(classType);
        } else {
            throw new ProxyRegisterException(classType.getName() + "'s instance hasn't been registed!");
        }
    }
}
