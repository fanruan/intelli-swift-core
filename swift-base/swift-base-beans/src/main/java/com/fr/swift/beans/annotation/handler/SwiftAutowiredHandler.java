package com.fr.swift.beans.annotation.handler;

import com.fr.swift.beans.factory.SwiftBeanDefinition;
import com.fr.swift.beans.factory.SwiftBeanRegistry;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

/**
 * @author anner
 * @this class created on date 19-8-14
 * @description
 */
public class SwiftAutowiredHandler implements BeanHandler {
    @Override
    public void handle(Object object, SwiftBeanDefinition beanDefinition) throws InvocationTargetException, IllegalAccessException {
        List<Field> fields = beanDefinition.getAllAutowiredFiles();
        String beanName = beanDefinition.getBeanName();
        for (Field field : fields) {
            field.setAccessible(true);
            String targetBeanName = beanDefinition.getAutowiredFields().get(field);
            Object targetObject = SwiftBeanRegistry.getInstance().getSingletonObjects().get(targetBeanName);
            field.set(object, targetObject);
        }
        SwiftBeanRegistry.getInstance().getSingletonObjects().put(beanName, object);
    }
}
