package com.fr.swift.beans.annotation.process;

import com.fr.swift.util.Crasher;

import java.beans.Introspector;
import java.util.HashSet;
import java.util.Set;

/**
 * This class created on 2018/11/26
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
public class SwiftClassUtil {

    /**
     * 获得所有接口
     *
     * @param clazz
     * @return
     */
    public static Set<Class<?>> getAllInterfacesAndSelf(Class<?> clazz) {
        Set<Class<?>> returnClasses = new HashSet<Class<?>>();
        if (!clazz.isInterface() && !clazz.equals(Object.class)) {
            returnClasses.add(clazz);
        }
        returnClasses.addAll(getAllInterfaces(clazz));
        return returnClasses;
    }

    private static Set<Class<?>> getAllInterfaces(Class<?> clazz) {
        Set<Class<?>> returnClasses = new HashSet<Class<?>>();
        if (clazz.isInterface()) {
            returnClasses.add(clazz);
        }
        for (Class<?> aClass : clazz.getInterfaces()) {
            returnClasses.addAll(getAllInterfaces(aClass));
        }
        if (clazz.getSuperclass() != null) {
            returnClasses.addAll(getAllInterfacesAndSelf(clazz.getSuperclass()));
        }
        return returnClasses;
    }

    /**
     * get className's short name
     *
     * @param className
     * @return
     */
    public static String getDefaultBeanName(String className) {
        if (className == null) {
            Crasher.crash("className is null");
        }
        int lastDotIndex = className.lastIndexOf(46);
        int nameEndIndex = className.indexOf("$$");
        if (nameEndIndex == -1) {
            nameEndIndex = className.length();
        }
        String shortName = className.substring(lastDotIndex + 1, nameEndIndex);
        shortName = shortName.replace('$', '.');
        String defaultName = Introspector.decapitalize(shortName);
        return defaultName;
    }
}
