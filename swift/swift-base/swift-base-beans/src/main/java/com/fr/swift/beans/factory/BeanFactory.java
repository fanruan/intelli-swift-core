package com.fr.swift.beans.factory;

/**
 * This class created on 2018/11/26
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
public interface BeanFactory {

    <T> T getBean(String beanName, Class<T> clazz, Object... params);

    <T> T getBean(Class<T> clazz, Object... params);

    Object getBean(String beanName, Object... params);

    boolean isSingleton(String beanName);

    Class<?> getType(String beanName);

    boolean isTypeMatch(String name, Class<?> typeToMatch);

    void registerPackages(String... packageNames);

}
