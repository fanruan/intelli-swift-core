package com.fr.swift.beans.factory;

import java.util.List;
import java.util.Map;

/**
 * This class created on 2018/11/28
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
public interface BeanRegistry {

    void registerBeanDefinition(String beanName, SwiftBeanDefinition beanDefinition);

    void removeBeanDefinition(String beanName);

    SwiftBeanDefinition getBeanDefinition(String beanName);

    boolean containsBeanDefinition(String beanName);

    Map<String, SwiftBeanDefinition> getBeanDefinitionMap();

    void registerBeanNamesByType(Class<?> clazz, String beanName);

    void removeBeanNamesByType(Class<?> clazz, String beanName);

    List<String> getBeanNamesByType(Class<?> clazz);
}
