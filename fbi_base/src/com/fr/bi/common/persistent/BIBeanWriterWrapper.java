package com.fr.bi.common.persistent;

import com.fr.bi.stable.utils.program.BINonValueUtils;

import java.lang.reflect.Field;

/**
 * Created by Connery on 2016/1/19.
 */
public class BIBeanWriterWrapper extends BIBeanWrapper {
    public BIBeanWriterWrapper(Object bean, Class beanClass) {
        super(bean, beanClass);
    }

    public Class getFieldClass(String fieldName) {
        Field field = getField(fieldName);
        BINonValueUtils.checkNull(field);
        return field.getType();

    }


}