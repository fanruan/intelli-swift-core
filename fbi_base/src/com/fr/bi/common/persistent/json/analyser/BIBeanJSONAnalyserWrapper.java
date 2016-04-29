package com.fr.bi.common.persistent.json.analyser;

import com.fr.bi.common.persistent.BIBeanReaderWrapper;
import com.fr.bi.stable.utils.program.BIConstructorUtils;
import com.fr.json.JSONException;

import java.lang.reflect.InvocationTargetException;

/**
 * Created by Connery on 2016/1/19.
 */
public class BIBeanJSONAnalyserWrapper extends BIBeanReaderWrapper {
    private String jsonKey;
    private Class genericType;

    public BIBeanJSONAnalyserWrapper(Object bean) {
        super(bean, bean.getClass());
    }

    public BIBeanJSONAnalyserWrapper(Class beanClass)
            throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        super(BIConstructorUtils.constructObject(beanClass), beanClass);
    }

    public Class getGenericType() {
        return genericType;
    }

    public void setGenericType(Class genericType) {
        this.genericType = genericType;
    }

    public Object analysisJSON(Object jsonContent) throws JSONException {
        if (isIterableType()) {
            return new JSONIteratorObjectAnalyser(this).analysisJSON(jsonContent);
        } else if (isMapType()) {
            return new JSONMapObjectAnalyser(this).analysisJSON(jsonContent);
        } else if (isArrayType()) {
            return new JSONArrayObjectAnalyser(this).analysisJSON(jsonContent);
        } else {
            return new JSONNormalObjectAnalyser(this).analysisJSON(jsonContent);
        }
    }
}