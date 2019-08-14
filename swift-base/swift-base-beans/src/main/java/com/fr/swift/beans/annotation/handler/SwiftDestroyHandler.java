package com.fr.swift.beans.annotation.handler;

import com.fr.swift.beans.annotation.SwiftDestroy;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author anner
 * @this class created on date 2019/8/9
 * @description 等待補充
 */
public class SwiftDestroyHandler implements BeanHandler {
    @Override
    public void handle(Object object, Class<?> clazz) throws InvocationTargetException, IllegalAccessException {
        Method[] methods = clazz.getMethods();
        for (Method method : methods) {
            SwiftDestroy destroy = method.getAnnotation(SwiftDestroy.class);
            if (destroy != null) {
                method.invoke(object);
                return;
            }
        }
    }
}
