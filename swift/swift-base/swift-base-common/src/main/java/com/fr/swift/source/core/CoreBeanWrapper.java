package com.fr.swift.source.core;

import com.fr.swift.util.BeanUtils;
import com.fr.swift.util.Util;

import java.beans.IntrospectionException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by pony on 2017/11/16.
 */
public class CoreBeanWrapper {
    protected Object bean;
    protected Class beanClass;

    public CoreBeanWrapper(Object bean, Class beanClass) {
        Util.requireNonNull(bean);
        this.bean = bean;
        this.beanClass = beanClass;
    }


    public List<Field> getSelfAndInheritFields() {
        return BeanUtils.fetchAllAttributes(beanClass);
    }

    /**
     * 获得全部对象的字段，不包含静态字段
     *
     * @return
     */
    public List<Field> getAllObjectFields() {
        ArrayList<Field> withoutStatic = new ArrayList<Field>();
        Iterator<Field> it = getSelfAndInheritFields().iterator();
        while (it.hasNext()) {
            Field field = it.next();
            if (!Modifier.isStatic(field.getModifiers())) {
                withoutStatic.add(field);
            }
        }
        return withoutStatic;
    }


    public Object getOriginalValue(Field field) throws IntrospectionException, IllegalAccessException, InvocationTargetException {
        /**
         *  不能通过活动get方法来获取value
         *  因为get方法可能对对象做了封装
         */
        Util.requireNonNull(field);
        field.setAccessible(true);
        return field.get(bean);
    }

    public List<Field> seekSpecificTaggedAllField(Class<? extends Annotation> annotationClass) {
        Iterator<Field> it = getAllObjectFields().iterator();
        List<Field> result = new ArrayList<Field>();
        while (it.hasNext()) {
            Field field = it.next();
            if (field.isAnnotationPresent(annotationClass)) {
                result.add(field);
            }
        }
        return result;
    }
}
