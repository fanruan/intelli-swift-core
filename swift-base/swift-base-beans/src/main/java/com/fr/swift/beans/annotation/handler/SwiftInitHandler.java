package com.fr.swift.beans.annotation.handler;

import com.fr.swift.beans.annotation.SwiftInitMethod;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author anner
 * @this class created on date 2019/8/9
 * @description initmethod的执行
 */
public class SwiftInitHandler implements BeanHandler {

    @Override
    public void handle(Object object, Class<?> clazz) throws InvocationTargetException, IllegalAccessException {
        Method[] methods = clazz.getMethods();
        for (Method method : methods) {
            SwiftInitMethod initMethod = method.getAnnotation(SwiftInitMethod.class);
            if (initMethod != null) {
                method.invoke(object);
                return;
            }
        }
    }
}
