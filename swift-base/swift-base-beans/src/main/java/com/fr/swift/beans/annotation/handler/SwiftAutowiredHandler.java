package com.fr.swift.beans.annotation.handler;

import com.fr.swift.beans.factory.SwiftBeanDefinition;
import com.fr.swift.beans.factory.SwiftBeanRegistry;
import com.fr.swift.log.SwiftLoggers;

import java.lang.reflect.Field;
import java.util.List;

/**
 * @author anner
 * @this class created on date 19-8-14
 * @description
 */
public class SwiftAutowiredHandler implements BeanHandler {
    @Override
    public void handle(Object object, SwiftBeanDefinition beanDefinition) {
        List<Field> fields = beanDefinition.getAllAutowiredFiles();
        String beanName = beanDefinition.getBeanName();
        for (Field field : fields) {
            field.setAccessible(true);
            String targetBeanName = beanDefinition.getAutowiredFields().get(field);
            Object targetObject = SwiftBeanRegistry.getInstance().getSingletonObjects().get(targetBeanName);
            try {
                field.set(object, targetObject);
            } catch (IllegalAccessException e) {
                SwiftLoggers.getLogger().error("the field is not accessible : " + field.getType() + " " + field.getName() + " to get in IllegalAccessException", e);
            }
        }
        SwiftBeanRegistry.getInstance().getSingletonObjects().put(beanName, object);
    }
}
