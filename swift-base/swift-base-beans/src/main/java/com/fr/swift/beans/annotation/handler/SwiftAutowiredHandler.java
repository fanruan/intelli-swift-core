package com.fr.swift.beans.annotation.handler;

import com.fr.swift.beans.annotation.SwiftAutoWired;
import com.fr.swift.beans.factory.SwiftBeanRegistry;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

/**
 * @author anner
 * @this class created on date 19-8-14
 * @description
 */
public class SwiftAutowiredHandler implements BeanHandler {
    @Override
    public void handle(Object object, Class<?> clazz) throws InvocationTargetException, IllegalAccessException {
        //绑定对象
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            SwiftAutoWired autoWired = field.getAnnotation(SwiftAutoWired.class);
            if (autoWired != null) {
                field.set(object, SwiftBeanRegistry.getInstance().getSingletonObjects().get(field.getName()));
            }
        }
    }
}
