package com.fr.swift.beans.annotation.handler;

import com.fr.swift.beans.factory.SwiftBeanDefinition;
import com.fr.swift.log.SwiftLoggers;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author anner
 * @this class created on date 2019/8/9
 * @description initmethod的执行
 */
public class SwiftInitHandler implements BeanHandler {

    @Override
    public void handle(Object object, SwiftBeanDefinition beanDefinition) {
        Method initMethod = beanDefinition.getInitMethod();
        if (initMethod != null) {
            try {
                initMethod.invoke(object);
            } catch (IllegalAccessException | InvocationTargetException e) {
                SwiftLoggers.getLogger().error("can not invoke the initMethod because of IllegalAccessException or InvocationTargetException", e);
            }
        }
    }

}
