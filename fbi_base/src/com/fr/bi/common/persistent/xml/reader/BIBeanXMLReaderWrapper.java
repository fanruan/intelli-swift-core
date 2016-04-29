package com.fr.bi.common.persistent.xml.reader;


import com.fr.bi.common.persistent.BIBeanReaderWrapper;
import com.fr.bi.stable.utils.program.BIFieldUtils;

import java.util.Map;

/**
 * Created by Connery on 2016/1/2.
 */
public class BIBeanXMLReaderWrapper extends BIBeanReaderWrapper {
    private Boolean isDisposed;

    public Boolean getDisposed() {
        return isDisposed;
    }

    public void setDisposed(Boolean disposed) {
        isDisposed = disposed;
    }

    public BIBeanXMLReaderWrapper(Object bean) {
        super(bean, bean.getClass());
        isDisposed = false;
    }

    public void setBean(Object bean) {
        this.bean = bean;
    }

    public XMLValueReader generateReader(Map<String, BIBeanXMLReaderWrapper> generatedBean) {
        if (BIFieldUtils.isBasicType(beanClass)) {
            return new XMLBasicValueReader(this, generatedBean);
        } else if (BIFieldUtils.isIterableType(beanClass)) {
            return new XMLCollectionValueReader(this, generatedBean);
        } else if (BIFieldUtils.isMapType(beanClass)) {
            return new XMLMapValueReader(this, generatedBean);
        } else if (BIFieldUtils.isArrayType(beanClass)) {
            return new XMLArrayValueReader(this, generatedBean);
        } else {
            return new XMLNormalValueReader(this, generatedBean);
        }
    }

}