package com.fr.swift.beans.factory;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;

/**
 * This class created on 2018/11/26
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
public interface BeanFactory {

    void init();

    <T> T getBean(String beanName, Class<T> clazz, Object... params);

    <T> T getBean(Class<T> clazz, Object... params);

    Object getBean(String beanName, Object... params);

    boolean isSingleton(String beanName);

    Class<?> getType(String beanName);

    boolean isTypeMatch(String name, Class<?> typeToMatch);

    void registerPackages(String... packageNames);

    Map<String, Object> getBeansByAnnotations(Class<? extends Annotation> annotation);

    List<Class<?>> getClassesByAnnotations(Class<? extends Annotation> annotation);

    void refresh(Class<?> clazz) throws InvocationTargetException, IllegalAccessException, ClassNotFoundException;

    void refreshAll() throws InvocationTargetException, IllegalAccessException, ClassNotFoundException;
}
