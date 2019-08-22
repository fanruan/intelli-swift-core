package com.fr.swift.beans.annotation.process;

import com.fr.swift.beans.annotation.SwiftAutoWired;
import com.fr.swift.beans.annotation.SwiftQualifier;
import com.fr.swift.beans.exception.NoSuchBeanException;
import com.fr.swift.beans.exception.SwiftBeanException;
import com.fr.swift.beans.factory.SwiftBeanDefinition;
import com.fr.swift.beans.factory.SwiftBeanRegistry;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author anner
 * @this class created on date 2019/8/8
 * @description autoWired初始化
 */
public class SwiftAutowiredProcesser implements BeanProcesser {
    @Override
    public void process(SwiftBeanDefinition beanDefinition) {
        Set<Field> fields = getAllFields(beanDefinition.getClazz());
        for (Field field : fields) {
            SwiftAutoWired target = field.getAnnotation(SwiftAutoWired.class);
            List<String> beanNames = SwiftBeanRegistry.getInstance().getBeanNamesByType(field.getType());
            if (beanNames.isEmpty() && target.required()) {
                throw new NoSuchBeanException(field.getType().getName());
            } else if (beanNames.size() == 1) {
                //单例且唯一
                beanDefinition.getAutowiredFields().put(field, beanNames.get(0));
            } else {
                //存在多个beanName，判断注解SwiftQualifier是否存在
                if (field.isAnnotationPresent(SwiftQualifier.class)) {
                    String name = field.getAnnotation(SwiftQualifier.class).name();
                    //如果qualifier的name不存在，报错
                    if (beanNames.contains(name)) {
                        beanDefinition.getAutowiredFields().put(field, name);
                    } else {
                        throw new NoSuchBeanException(String.format(" qualifier name: %s is not existed", name));
                    }
                } else {
                    throw new SwiftBeanException(String.format("%s contains >=2 beanNames", beanDefinition.getClazz().getName()));
                }
            }
        }
        if (beanDefinition.getAutowiredFields().size() > 0) {
            beanDefinition.setAutoWired(true);
        }
    }

    public Set<Field> getAllFields(Class<?> clazz) {
        Set<Class<?>> superClasses = SwiftClassUtil.getAllInterfacesAndSelf(clazz);
        Set<Field> autowiredFields = new HashSet<>();
        for (Class<?> superClass : superClasses) {
            if (superClass.isInterface()) {
                continue;
            }
            Field[] fields = superClass.getDeclaredFields();
            for (Field field : fields) {
                if (field.isAnnotationPresent(SwiftAutoWired.class)) {
                    autowiredFields.add(field);
                }
            }
        }
        return autowiredFields;
    }

}
