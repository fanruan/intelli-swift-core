package com.fr.swift.beans.annotation.process;

import com.fr.swift.beans.annotation.SwiftScope;
import com.fr.swift.beans.factory.SwiftBeanDefinition;

/**
 * @author anner
 * @this class created on date 2019/8/8
 * @description
 */
public class SwiftScopeProcesser implements BeanProcesser {
    @Override
    public void process(SwiftBeanDefinition beanDefinition) {
        if (beanDefinition.getClazz().isAnnotationPresent(SwiftScope.class)) {
            SwiftScope swiftScope = beanDefinition.getClazz().getAnnotation(SwiftScope.class);
            String beanName = beanDefinition.getBeanName();
            if (swiftScope == null || swiftScope.value().equals(SwiftScope.SINGLETON)) {
                beanDefinition.setScope(SwiftScope.SINGLETON);
            } else if (swiftScope.value().equals(SwiftScope.PROTOTYPE)) {
                beanDefinition.setScope(SwiftScope.PROTOTYPE);
            }
        }
    }
}
