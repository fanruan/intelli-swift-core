package com.fr.swift.beans.annotation.process;

import com.fr.swift.beans.annotation.SwiftDestroy;
import com.fr.swift.beans.factory.SwiftBeanDefinition;

import java.lang.reflect.Method;

/**
 * @author anner
 * @this class created on date 2019/8/9
 * @description
 */
public class SwiftDestroyProcesser implements BeanProcesser {
    @Override
    public void process(SwiftBeanDefinition beanDefinition) {
        Method[] methods = beanDefinition.getClazz().getMethods();
        for (Method method : methods) {
            SwiftDestroy destroy = method.getAnnotation(SwiftDestroy.class);
            if (destroy != null) {
                beanDefinition.setDestroyMethod(method.getName());
                return;
            }
        }
    }
}
