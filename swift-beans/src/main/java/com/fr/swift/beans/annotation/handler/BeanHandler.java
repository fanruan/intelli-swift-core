package com.fr.swift.beans.annotation.handler;

import com.fr.swift.beans.factory.SwiftBeanDefinition;

/**
 * @author anner
 * @this interface created on date 2019/8/9
 * @description
 */
public interface BeanHandler {
    void handle(Object object, SwiftBeanDefinition beanDefinition);
}
