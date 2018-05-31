package com.fr.swift.frrpc;

import com.fr.swift.exception.ProxyNotRegisteException;

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

    public static Object getProxy(Class classType) throws ProxyNotRegisteException {
        if (INSTANCE.proxyMap.containsKey(classType)) {
            return INSTANCE.proxyMap.get(classType);
        } else {
            throw new ProxyNotRegisteException(classType.getName() + "'s proxy hasn't been registed!");
        }
    }

    public static void registerInstance(Class classType, Object object) {
        INSTANCE.instanceMap.put(classType, object);
    }

    public static Object getInstance(Class classType) throws ProxyNotRegisteException {
        if (INSTANCE.instanceMap.containsKey(classType)) {
            return INSTANCE.instanceMap.get(classType);
        } else {
            throw new ProxyNotRegisteException(classType.getName() + "'s instance hasn't been registed!");
        }
    }
}
