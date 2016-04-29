package com.fr.bi.common.persistent;

import com.fr.bi.stable.utils.program.BINonValueUtils;
import com.fr.bi.stable.utils.program.BITypeUtils;
import com.fr.general.ComparatorUtils;

import java.beans.IntrospectionException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by Connery on 2016/1/19.
 */
public class BIBeanReaderWrapper extends BIBeanWrapper {
    public BIBeanReaderWrapper(Object bean, Class beanClass) {
        super(bean, beanClass);
    }

    public void setOriginalValue(String fieldName, Object value) throws IntrospectionException, IllegalAccessException, InvocationTargetException {
        Field field = getField(fieldName);
        setOriginalValue(field, value);
    }

    public void setOriginalValue(Field field, Object value) throws IntrospectionException, IllegalAccessException, InvocationTargetException {
        BINonValueUtils.checkNull(field);
        field.setAccessible(true);
        if (BITypeUtils.isPrimitiveType(field.getType())) {
            setPrimitiveValue(field, value);
        } else {
            field.set(bean, value);
        }
    }

    public Method getWriter(Field field) throws IntrospectionException {
        return getPropertyDescriptor(field).getWriteMethod();
    }

    private void setPrimitiveValue(Field field, Object value) throws IntrospectionException, IllegalAccessException, InvocationTargetException {

        if (ComparatorUtils.equals(field.getType(), int.class)) {
            field.set(bean, ((Integer) value).intValue());
        } else if (ComparatorUtils.equals(field.getType(), long.class)) {
            field.set(bean, ((Long) value).longValue());
        } else if (ComparatorUtils.equals(field.getType(), double.class)) {
            field.set(bean, ((Double) value).doubleValue());
        } else if (ComparatorUtils.equals(field.getType(), float.class)) {
            field.set(bean, ((Float) value).floatValue());
        } else if (ComparatorUtils.equals(field.getType(), char.class)) {
            field.set(bean, ((Character) value).charValue());
        } else if (ComparatorUtils.equals(field.getType(), byte.class)) {
            field.set(bean, ((Byte) value).byteValue());
        } else if (ComparatorUtils.equals(field.getType(), short.class)) {
            field.set(bean, ((Short) value).shortValue());
        } else if (ComparatorUtils.equals(field.getType(), boolean.class)) {
            field.set(bean, ((Boolean) value).booleanValue());
        }
    }
}