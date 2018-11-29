package com.fr.swift.config.oper.impl;

import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.util.Crasher;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * @author yee
 * @date 2018-11-28
 */
public final class ConfigOrder {
    private static final Map<String, Method> nameToMethod = new HashMap<String, Method>();

    static {
        try {
            Method asc = VersionConfigProperty.get().getOrder().getDeclaredMethod("asc", String.class);
            Method desc = VersionConfigProperty.get().getOrder().getDeclaredMethod("desc", String.class);
            nameToMethod.put("asc", asc);
            nameToMethod.put("desc", desc);
        } catch (NoSuchMethodException e) {
            SwiftLoggers.getLogger().error(e);
        }
    }

    private static Object invoke(String methodName, Object... args) throws Exception {
        return getObject(methodName, nameToMethod, args);
    }

    static Object getObject(String methodName, Map<String, Method> nameToMethod, Object[] args) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        Method method = nameToMethod.get(methodName);
        if (method != null) {
            return method.invoke(null, method, args);
        }
        throw new NoSuchMethodException(methodName);
    }

    public static Object asc(String propertyName) {
        try {
            return invoke("asc", propertyName);
        } catch (Exception e) {
            return Crasher.crash(e);
        }
    }

    public static Object desc(String propertyName) {
        try {
            return invoke("desc", propertyName);
        } catch (Exception e) {
            return Crasher.crash(e);
        }
    }
}
