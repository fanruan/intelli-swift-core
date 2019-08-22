package com.fr.swift.beans.annotation.handler;

import com.fr.swift.beans.factory.SwiftBeanDefinition;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author anner
 * @this class created on date 2019/8/9
 * @description initmethod的执行
 */
public class SwiftInitHandler implements BeanHandler {

    @Override
    public void handle(Object object, SwiftBeanDefinition beanDefinition) throws InvocationTargetException, IllegalAccessException {
        Method initMethod = beanDefinition.getInitMethod();
        if (initMethod != null) {
            initMethod.invoke(object);
        }
    }

}
