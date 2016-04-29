package com.fr.bi.common.persistent;

import com.fr.bi.stable.utils.program.BIBeanUtils;
import com.fr.bi.stable.utils.program.BIFieldUtils;
import com.fr.bi.stable.utils.program.BINonValueUtils;
import com.fr.bi.stable.utils.program.BITypeUtils;
import com.fr.general.ComparatorUtils;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Connery on 2016/1/2.
 */
public class BIBeanWrapper {
    protected Object bean;
    protected Class beanClass;
    /**
     * 当前Bean是作为属性时，该属性对象
     */
    protected Field propertyField;

    public BIBeanWrapper(Object bean, Class beanClass) {
        BINonValueUtils.checkNull(bean);
        this.bean = bean;
        this.beanClass = beanClass;
    }

    public Field getPropertyField() {
        return propertyField;
    }

    public void setPropertyField(Field propertyField) {
        this.propertyField = propertyField;
    }

    public Object getBean() {
        return bean;
    }

    public String getClassName() {
        return beanClass.getName();
    }

    public Class getBeanClass() {
        return beanClass;
    }

    public List<Field> getSelfAndInheritFields() {
        return BIBeanUtils.fetchAllAttributes(beanClass);
    }

    public List<Field> getSelfFields() {
        return BIBeanUtils.fetchAttributes(beanClass);
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

    public Field getField(String fieldName) {
        Iterator<Field> it = getSelfAndInheritFields().iterator();
        while (it.hasNext()) {
            Field field = it.next();
            if (ComparatorUtils.equals(field.getName(), fieldName)) {
                return field;
            }
        }
        return null;
    }


    public PropertyDescriptor getPropertyDescriptor(Field field) throws IntrospectionException {
        BINonValueUtils.checkNull(field);
        return new PropertyDescriptor(field.getName(), beanClass);
    }

    public List<Field> getPropertyDescriptorFields() {
        ArrayList<Field> result = new ArrayList<Field>();
        Iterator<Field> fieldIterator = getSelfAndInheritFields().iterator();
        while (fieldIterator.hasNext()) {
            Field field = fieldIterator.next();
            try {
                getPropertyDescriptor(field);
                result.add(field);
            } catch (Exception e) {
                continue;
            }
        }
        return result;
    }

    public Object getOriginalValue(Field field) throws IntrospectionException, IllegalAccessException, InvocationTargetException {
        /**
         *  不能通过活动get方法来获取value
         *  因为get方法可能对对象做了封装
         */
        BINonValueUtils.checkNull(field);
        field.setAccessible(true);
        return field.get(bean);
    }

    public Object getOriginalValue(String fieldName) throws IntrospectionException, IllegalAccessException, InvocationTargetException {
        return getOriginalValue(getField(fieldName));
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

    public List<Field> seekBasicTypeField() {
        Iterator<Field> it = getAllObjectFields().iterator();
        List<Field> result = new ArrayList<Field>();
        while (it.hasNext()) {
            Field field = it.next();
            if (BITypeUtils.isBasicValue(field.getType())) {
                result.add(field);
            }
        }
        return result;
    }

    protected Boolean isArrayType() {
        return BIFieldUtils.isArrayType(beanClass);
    }

    protected Boolean isMapType() {
        return BIFieldUtils.isMapType(beanClass);
    }

    protected Boolean isIterableType() {
        return BIFieldUtils.isIterableType(beanClass);
    }

    protected Boolean isBasicType() {
        return BIFieldUtils.isBasicType(beanClass);
    }
}