package com.fr.swift.netty.rpc.server;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author yee
 * @date 2018/10/8
 */
public enum ServiceMethodRegistry {
    /**
     * 单例
     */
    INSTANCE;
    private Map<String, Method> methodMap = new ConcurrentHashMap<String, Method>();

    public void registerMethod(String methodName, Method method) {
        methodMap.put(methodName, method);
    }

    public Method getMethodByName(String methodName) {
        return methodMap.get(methodName);
    }

    public Map<String, Method> getMethodNames() {
        return new HashMap<String, Method>(methodMap);
    }
}
