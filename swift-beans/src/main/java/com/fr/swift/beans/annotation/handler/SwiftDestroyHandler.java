package com.fr.swift.beans.annotation.handler;

import com.fr.swift.beans.factory.SwiftBeanDefinition;
import com.fr.swift.log.SwiftLoggers;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author anner
 * @this class created on date 2019/8/9
 * @description
 */
public class SwiftDestroyHandler implements BeanHandler {
    @Override
    public void handle(Object object, SwiftBeanDefinition beanDefinition) {
        Method destroyMethod = beanDefinition.getDestroyMethod();
        if (destroyMethod != null) {
            try {
                destroyMethod.invoke(object);
            } catch (IllegalAccessException | InvocationTargetException e) {
                SwiftLoggers.getLogger().error("can not invoke the destroyMethod because of IllegalAccessException or InvocationTargetException", e);
            }
        }
    }
}
