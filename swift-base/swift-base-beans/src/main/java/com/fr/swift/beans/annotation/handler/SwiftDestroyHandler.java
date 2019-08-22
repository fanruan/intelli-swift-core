package com.fr.swift.beans.annotation.handler;

import com.fr.swift.beans.factory.SwiftBeanDefinition;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author anner
 * @this class created on date 2019/8/9
 * @description
 */
public class SwiftDestroyHandler implements BeanHandler {
    @Override
    public void handle(Object object, SwiftBeanDefinition beanDefinition) throws InvocationTargetException, IllegalAccessException {
        Method destroyMethod = beanDefinition.getDestroyMethod();
        if (destroyMethod != null) {
            destroyMethod.invoke(object);
        }
    }
}
