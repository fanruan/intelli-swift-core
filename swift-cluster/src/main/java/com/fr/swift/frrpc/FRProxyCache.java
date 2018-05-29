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

    private FRProxyCache() {
        this.proxyMap = new ConcurrentHashMap<Class, Object>();
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
}
