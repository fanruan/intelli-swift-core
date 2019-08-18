package com.fr.swift.beans.annotation.handler;

import com.fr.swift.beans.annotation.SwiftBean;
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
    public void handle(Object object, Class<?> clazz) throws InvocationTargetException, IllegalAccessException {
        String beanName = clazz.getAnnotation(SwiftBean.class).name();
        SwiftBeanDefinition beanDefinition = SwiftBeanRegistry.getInstance().getBeanDefinition(beanName);
        List<Field> fields = beanDefinition.getAllAutowiredFiles();
        for (Field field : fields) {
            field.setAccessible(true);
            String targetBeanName = beanDefinition.getAutowiredFields().get(field);
            Object targetObject = SwiftBeanRegistry.getInstance().getSingletonObjects().get(targetBeanName);
            field.set(object, targetObject);
        }
        SwiftBeanRegistry.getInstance().getSingletonObjects().put(beanName, object);
    }
}
