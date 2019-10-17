package com.fr.swift.beans.annotation.process;

import com.fr.swift.beans.annotation.SwiftInitMethod;
import com.fr.swift.beans.factory.SwiftBeanDefinition;

import java.lang.reflect.Method;

/**
 * @author anner
 * @this class created on date 2019/8/9
 * @description 初始化init注解
 */
public class SwiftInitProcesser implements BeanProcesser {
    @Override
    public void process(SwiftBeanDefinition beanDefinition) {
        Method[] methods = beanDefinition.getClazz().getMethods();
        for (Method method : methods) {
            if (method.isAnnotationPresent(SwiftInitMethod.class)) {
                beanDefinition.setInitMethod(method);
            }
        }
    }
}
