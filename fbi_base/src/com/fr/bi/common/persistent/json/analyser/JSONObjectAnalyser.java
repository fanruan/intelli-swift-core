package com.fr.bi.common.persistent.json.analyser;


import com.fr.bi.common.factory.BISampleFactory;
import com.fr.bi.common.persistent.BIBeanWrapper;
import com.fr.bi.common.persistent.json.analyser.anno.BIElementType;
import com.fr.bi.common.persistent.json.analyser.anno.BISampleFactoryKeyField;
import com.fr.bi.common.persistent.json.generator.BIKeyFieldSizeException;
import com.fr.bi.stable.utils.program.BIConstructorUtils;
import com.fr.bi.stable.utils.program.BITypeUtils;
import com.fr.json.JSONArray;
import com.fr.json.JSONException;
import com.fr.json.JSONObject;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

/**
 * Created by Connery on 2016/1/19.
 */
public abstract class JSONObjectAnalyser {
    protected BIBeanJSONAnalyserWrapper analyserWrapper;

    public JSONObjectAnalyser(BIBeanJSONAnalyserWrapper analyserWrapper) {
        this.analyserWrapper = analyserWrapper;
    }

    public Object analysisJSON(Object jsonContent) throws JSONException {
        Object temp;
        if (jsonContent instanceof String) {
            if (this instanceof JSONIteratorObjectAnalyser) {
                temp = new JSONArray((String)jsonContent);
            } else {
                temp = new JSONObject((String)jsonContent);
            }
        } else if (!(jsonContent instanceof JSONArray || jsonContent instanceof JSONObject)) {
            throw new JSONException("Iterator json Content type is inaccuracy ");
        } else {
            temp = jsonContent;
        }
        return analysisJSONContent(temp);
    }

    public abstract Object analysisJSONContent(Object jsonContent) throws JSONException;


    protected BIBeanJSONAnalyserWrapper constructFieldObj(Field field, String fieldJSONObjectStr) throws
            NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException,
            JSONException, BIFactoryKeyFieldException, BIKeyFieldSizeException {
        if (field.isAnnotationPresent(BIElementType.class)) {
            return constructTaggedField(field, fieldJSONObjectStr);
        } else {
            return new BIBeanJSONAnalyserWrapper(BIConstructorUtils.constructObject(field.getType()));
        }
    }

    protected BIBeanJSONAnalyserWrapper constructTaggedField(Field field, String fieldJSONObjectStr) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException, JSONException, BIFactoryKeyFieldException, BIKeyFieldSizeException {
        BIElementType elementType = field.getAnnotation(BIElementType.class);
        Class implemented = elementType.implemented();
        Class genericType = elementType.genericType();
        if ((BITypeUtils.isAssignable(BISampleFactory.class, implemented))) {
            BISampleFactory factory = (BISampleFactory) BIConstructorUtils.constructObject(implemented);
            return constructObjByFactory(field, factory, fieldJSONObjectStr);
        } else {
            /**
             * 判断指定的类，是否是当前字段类型的子类
             */
            if (BITypeUtils.isAssignable(field.getType(), implemented)) {
                BIBeanJSONAnalyserWrapper analyserWrapper = new BIBeanJSONAnalyserWrapper(BIConstructorUtils.constructObject(implemented));
                analyserWrapper.setGenericType(genericType);
                return analyserWrapper;
            } else {
                throw new ClassCastException("the annotation is :" + implemented.getName() + ",but field" +
                        " type is:" + field.getType().getName() + " ,please check!");
            }
        }
    }

    protected BIBeanJSONAnalyserWrapper constructObjByFactory(Field field, BISampleFactory factory, String fieldJSONObjectStr)
            throws JSONException, BIFactoryKeyFieldException, BIKeyFieldSizeException {
        Class fieldType = field.getType();
        JSONObject fieldJSONObject = new JSONObject(fieldJSONObjectStr);
        BIBeanWrapper wrapper = new BIBeanWrapper(new Object(), fieldType);
        List<Field> fields = wrapper.seekSpecificTaggedAllField(BISampleFactoryKeyField.class);
        if (fields.size() == 1) {
            Field factoryKey = fields.get(0);
            if (BITypeUtils.isBasicValue(factoryKey.getType())) {
                Class factoryKeyClass = factoryKey.getType();
                String factoryKeyValueStr = fieldJSONObject.getString(factoryKey.getName());
                Object factoryKeyValue = BITypeUtils.stringConvert2BasicType(factoryKeyClass, factoryKeyValueStr);
                return new BIBeanJSONAnalyserWrapper(factory.getSpecificObject(factoryKeyValue));

            } else {
                throw new BIFactoryKeyFieldException("The Class :" + fieldType + " must choose one basic type be factory key");
            }
        } else {
            throw new BIKeyFieldSizeException("The Class :" + fieldType + "should have one factoryKey");
        }
    }
}