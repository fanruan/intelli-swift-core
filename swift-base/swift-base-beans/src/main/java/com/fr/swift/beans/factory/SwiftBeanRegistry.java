package com.fr.swift.beans.factory;

import com.fr.swift.beans.exception.SwiftBeanException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This class created on 2018/11/28
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
public class SwiftBeanRegistry implements BeanRegistry {

    private final static SwiftBeanRegistry INSTANCE = new SwiftBeanRegistry();

    private SwiftBeanRegistry() {
    }

    public static SwiftBeanRegistry getInstance() {
        return INSTANCE;
    }


    private final Map<String, SwiftBeanDefinition> swiftBeanDefinitionMap = new ConcurrentHashMap<String, SwiftBeanDefinition>();

    private final Map<Class<?>, List<String>> allBeanNamesByType = new ConcurrentHashMap<Class<?>, List<String>>();

    private Map<String, Object> singletonObjects;

    @Override
    public void registerBeanDefinition(String beanName, SwiftBeanDefinition beanDefinition) {
        synchronized (swiftBeanDefinitionMap) {
            if (swiftBeanDefinitionMap.containsKey(beanName)) {
                throw new SwiftBeanException(beanName, SwiftBeanException.Type.EXIST);
            }
            swiftBeanDefinitionMap.put(beanName, beanDefinition);
        }
    }

    @Override
    public void removeBeanDefinition(String beanName) {
        synchronized (swiftBeanDefinitionMap) {
            if (!swiftBeanDefinitionMap.containsKey(beanName)) {
                throw new SwiftBeanException(beanName, SwiftBeanException.Type.NOT_EXIST);
            }
            swiftBeanDefinitionMap.remove(beanName);
        }
    }

    @Override
    public SwiftBeanDefinition getBeanDefinition(String beanName) {
        if (!swiftBeanDefinitionMap.containsKey(beanName)) {
            throw new SwiftBeanException(beanName, SwiftBeanException.Type.NOT_EXIST);
        }
        return swiftBeanDefinitionMap.get(beanName);
    }

    @Override
    public boolean containsBeanDefinition(String beanName) {
        return swiftBeanDefinitionMap.containsKey(beanName);
    }

    @Override
    public Map<String, SwiftBeanDefinition> getBeanDefinitionMap() {
        return new HashMap<String, SwiftBeanDefinition>(swiftBeanDefinitionMap);
    }

    @Override
    public void registerBeanNamesByType(Class<?> clazz, String beanName) {
        synchronized (allBeanNamesByType) {
            if (!allBeanNamesByType.containsKey(clazz)) {
                allBeanNamesByType.put(clazz, new ArrayList<String>());
            }
            if (!allBeanNamesByType.get(clazz).contains(beanName)) {
                allBeanNamesByType.get(clazz).add(beanName);
            }
        }
    }

    @Override
    public void removeBeanNamesByType(Class<?> clazz, String beanName) {
        synchronized (allBeanNamesByType) {
            if (allBeanNamesByType.containsKey(clazz)) {
                allBeanNamesByType.get(clazz).remove(beanName);
            }
        }
    }

    @Override
    public List<String> getBeanNamesByType(Class<?> clazz) throws SwiftBeanException {
        if (!allBeanNamesByType.containsKey(clazz)) {
            throw new SwiftBeanException(clazz.getName() + " has no bean names!");
        }
        return new ArrayList<String>(allBeanNamesByType.get(clazz));
    }

    public Map<String, Object> getSingletonObjects() {
        return singletonObjects;
    }

    public void setSingletonObjects(Map<String, Object> singletonObjects) {
        this.singletonObjects = singletonObjects;
    }
}
