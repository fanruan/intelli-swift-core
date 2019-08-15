package com.fr.swift.beans.annotation.process;

import com.fr.swift.beans.annotation.SwiftAutoWired;
import com.fr.swift.beans.annotation.SwiftQualifier;
import com.fr.swift.beans.exception.NoSuchBeanException;
import com.fr.swift.beans.exception.SwiftBeanException;
import com.fr.swift.beans.factory.SwiftBeanDefinition;
import com.fr.swift.beans.factory.SwiftBeanRegistry;

import java.lang.reflect.Field;
import java.util.List;

/**
 * @author anner
 * @this class created on date 2019/8/8
 * @description autoWired初始化
 */
public class SwiftAutowiredProcesser implements BeanProcesser {
    @Override
    public void process(SwiftBeanDefinition beanDefinition) {
        //判断注解在属性上的位置
        Field[] fields = beanDefinition.getClazz().getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            SwiftAutoWired target = field.getAnnotation(SwiftAutoWired.class);
            if (target != null) {
                //判断注解对象是否被托管
                List<String> beanNames = SwiftBeanRegistry.getInstance().getBeanNamesByType(field.getType());
                if (beanNames.isEmpty()) {
                    throw new NoSuchBeanException(field.getType().getName());
                } else if (beanNames.size() == 1) {
                    //单例且唯一
                    beanDefinition.getAutowiredClassList().add(field.getType());
                } else {
                    //存在多个bean，判断注解SwiftQualifier是否存在
                    if (field.getAnnotation(SwiftQualifier.class) != null) {
                        beanDefinition.getAutowiredClassList().add(field.getType());
                    } else {
                        throw new SwiftBeanException(beanDefinition.getClazz().getName() + " contains >=2 beanNames");
                    }
                }
            }
            if (beanDefinition.getAutowiredClassList().size() > 0) {
                beanDefinition.setAutoWired(true);
            }
        }
    }

}
