package com.fr.bi.common.persistent.json;

import com.fr.bi.stable.utils.code.BILogger;
import com.fr.bi.stable.utils.program.BIBeanUtils;
import com.fr.bi.stable.utils.program.BIFieldUtils;
import com.fr.bi.stable.utils.program.BITypeUtils;
import com.fr.json.JSONArray;
import com.fr.json.JSONException;
import com.fr.json.JSONObject;
import com.fr.json.JSONTransform;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Json对象是将对象内部的属性和相应的值转换成Json格式的数据
 * 在转换过程如果数据为空，那么就是Null，而不应该再从其它数据源获取数据。
 * 例如在createJson的过程中存在从数据库取值的操作
 * 更不应该在create过程中做初始化。所有此类操作都应该放在对象构造的时候
 * 完成。
 * <p/>
 * 主动从对象生成json数据和从json数据解析对象属性
 * 需要使用该功能的对象的必须是BIJsonObject的子类
 * 并且符合java bean规范。
 * 当前支持的属性类型：
 * 基础类型，装包类型，String,数组类型，Iterable类型，Map类型，BIJsonObject子类
 * <p/>
 * 处理规则：
 * 处理函数f:
 * <p/>
 * if(field 的类型是基础类型，装包类型，String){
 * 都是fieldName作为json的key，对应的值作为value
 * }else if(field 的类型是Map){
 * fieldName作为json的key，对应的值为以下的jsonObject
 * jsonObject为： f(map中的key)作为key，f（map中value）作为value
 * }
 * <p/>
 * Created by Connery on 2015/12/18.
 */
public class BIJSONObject implements JSONTransform {


 /*   public static void main(String[] args) {
        System.out.println(BIJsonObject.class.getClass().toString());
        System.out.println(BIJsonObject.class.getClass().getInterfaces());
        System.out.println(BIJsonObject.class.toString());
        System.out.println(int.class.toString());
    }*/

    @Override
    public JSONObject createJSON() throws Exception {
        return createJSON(new HashMap<String, ArrayList<Object>>(), false);
    }

    @Override
    public void parseJSON(JSONObject jsonObject) throws Exception {

    }

    private Class getCurrentClass() {
        return getClass();
    }

    private Object parseJSON2(JSONObject jsonObject) {
        Class clazz = getCurrentClass();
        ArrayList<Field> fields = BIBeanUtils.fetchAllAttributes(clazz, BIJSONObject.class);
        Iterator<Field> it = fields.iterator();
        while (it.hasNext()) {
            Field field = it.next();
            try {
                Class fieldClazz = field.getType();
                String fieldName = field.getName();
                PropertyDescriptor propertyDescriptor = new PropertyDescriptor(field.getName(), clazz);
                Method method = propertyDescriptor.getWriteMethod();
                if (BIFieldUtils.isBasicType(fieldClazz)) {
                    method.invoke(this, readJsonValue(jsonObject, fieldClazz, fieldName));
                } else if (BIFieldUtils.isArrayType(fieldClazz)) {

                }
            } catch (Exception e) {
                BILogger.getLogger().error(e.getMessage(), e);
            }
        }
        return null;
    }

    /**
     * @param disposedBeans 遍历过的全部对象
     * @param isSampleModel 是否简单模式，简单模式只是处理是基础赋值的类型的属性，引用类型舍弃
     * @return
     * @throws Exception
     */
    public JSONObject createJSON(Map<String, ArrayList<Object>> disposedBeans, Boolean isSampleModel) throws Exception {
        Class clazz = getCurrentClass();
        /**
         * 这个地方要在进入createJson方法时候设置
         * 否则在属性内部如果存在指向自身的，那么就出现
         * 死循环
         */
        setDisposed(disposedBeans, this);
        ArrayList<Field> fields = BIBeanUtils.fetchAllAttributes(clazz, BIJSONObject.class);
        JSONObject resultJson = new JSONObject();
        Iterator<Field> it = fields.iterator();
        while (it.hasNext()) {
            Field field = it.next();
            try {
                PropertyDescriptor propertyDescriptor = new PropertyDescriptor(field.getName(), clazz);
                Method method = propertyDescriptor.getReadMethod();
                Object fieldValue = method.invoke(this);
                if (fieldValue != null) {
                    /**
                     * 简单模式部分
                     */
                    if (BIFieldUtils.isBasicValue(field)) {
                        resultJson = writeJsonValue(resultJson, field.getName(), fieldValue);
                    }
                    if (!isSampleModel) {
                        if (isBIJsonObject(fieldValue)) {
                            resultJson = writeJsonValue(resultJson, field.getName(), createBIJSONObject(disposedBeans, fieldValue));
                        } else if (BIFieldUtils.isIterableObject(fieldValue)) {
                            resultJson = writeJsonValue(resultJson, field.getName(), createIterableJSON(disposedBeans, fieldValue));
                        } else if (BIFieldUtils.isArrayObject(fieldValue)) {
                            resultJson = writeJsonValue(resultJson, field.getName(), createArrayJSON(disposedBeans, fieldValue));
                        } else if (BIFieldUtils.isMapObject(fieldValue)) {
                            resultJson = writeJsonValue(resultJson, field.getName(), createMapJSON(disposedBeans, fieldValue));
                        }
                    }
                } else {
                    continue;
                }
            } catch (Exception e) {
                BILogger.getLogger().error(e.getMessage(), e);
                continue;
            }
        }
        return resultJson;
    }

    private JSONObject writeJsonValue(JSONObject jsonObject, String jsonKey, Object jsonValue) throws JSONException {
        return jsonObject.put(jsonKey, jsonValue);
    }

    private <T> T readJsonValue(JSONObject jsonObject, Class<T> fieldType, String fieldName) throws JSONException {
        Object result = new Object();
        /**
         * 不可以是String.class.isAssignableFrom(fieldType)。
         * 否则的话，fieldType可以是String的子类，上面的判断也是true。
         * 这里fieldType必须是String类型。
         */
        if (BITypeUtils.isAssignable(fieldType, String.class)) {
            result = jsonObject.getString(fieldName);
        } else if (BITypeUtils.isAssignable(fieldType, Integer.class) && (BITypeUtils.isAssignable(fieldType, int.class))) {
            result = jsonObject.getInt(fieldName);
        } else if (BITypeUtils.isAssignable(fieldType, Long.class) && BITypeUtils.isAssignable(fieldType, long.class)) {
            result = jsonObject.getLong(fieldName);
        } else if (BITypeUtils.isAssignable(fieldType, Boolean.class) && BITypeUtils.isAssignable(fieldType, boolean.class)) {
            result = jsonObject.getBoolean(fieldName);
        } else if (BITypeUtils.isAssignable(fieldType, Double.class) && BITypeUtils.isAssignable(fieldType, double.class)) {
            result = jsonObject.getDouble(fieldName);
        } else if (BITypeUtils.isAssignable(fieldType, Float.class) && BITypeUtils.isAssignable(fieldType, float.class)) {
            result = jsonObject.getFloat(fieldName);
        }
        return (T) result;
    }

    private JSONObject createFieldJSON(Map<String, ArrayList<Object>> disposedBeans, Object fieldValue) throws Exception {
        if (!isDisposed(disposedBeans, fieldValue)) {
            return ((BIJSONObject) fieldValue).createJSON(disposedBeans, false);
        } else {
            return ((BIJSONObject) fieldValue).createJSON(disposedBeans, true);
        }
    }

//    private JSONObject createReferenceFieldJson(Map<String, ArrayList<Object>> disposedBeans, Object fieldValue) throws Exception {
//        if (!isDisposed(disposedBeans, fieldValue)) {
//            return ((BIJsonObject) fieldValue).createJSON(disposedBeans, false);
//        } else {
//            return ((BIJsonObject) fieldValue).createJSON(disposedBeans, true);
//        }
//    }

    private JSONObject createBIJSONObject(Map<String, ArrayList<Object>> disposedBeans, Object fieldValue) throws Exception {
        if (isBIJsonObject(fieldValue)) {
            return createFieldJSON(disposedBeans, fieldValue);
        }
        return new JSONObject();
    }

    private Object parseBIJSONObject(JSONObject jsonObject, Class fieldClazz) throws Exception {
        if (isBIJsonType(fieldClazz)) {
            return parseJSON2(jsonObject);
        }
        return new JSONObject();
    }

    private JSONArray createArrayJSON(Map<String, ArrayList<Object>> disposedBeans, Object fieldValue) throws Exception {
        if (BIFieldUtils.isArrayObject(fieldValue)) {
            JSONArray result = new JSONArray();
            Object[] array = (Object[]) fieldValue;
            for (int i = 0; i < array.length; i++) {
                result.put(createFieldJSON(disposedBeans, array[i]));
            }
            return result;
        } else {
            return new JSONArray();
        }
    }

    private JSONArray parseArrayJSON(JSONObject jsonObject, Class fieldClazz, String fieldName) throws Exception {
        if (BIFieldUtils.isArrayType(fieldClazz)) {
            JSONArray array = jsonObject.getJSONArray(fieldName);
            for (int i = 0; i < array.length(); i++) {
                Object object = array.get(i);
            }
            return new JSONArray();
        } else {
            return new JSONArray();
        }
    }

    private JSONArray createIterableJSON(Map<String, ArrayList<Object>> disposedBeans, Object fieldValue) throws Exception {
        if (BIFieldUtils.isIterableObject(fieldValue)) {
            JSONArray result = new JSONArray();
            Iterator it = ((Iterable) fieldValue).iterator();
            while (it.hasNext()) {
                result.put(createFieldJSON(disposedBeans, it.next()));
            }
            return result;
        } else {
            return new JSONArray();
        }
    }

    private JSONObject createMapJSON(Map<String, ArrayList<Object>> disposedBeans, Object fieldValue) throws Exception {
        if (BIFieldUtils.isMapObject(fieldValue)) {
            JSONObject result = new JSONObject();
            Iterator it = ((Map) fieldValue).keySet().iterator();
            while (it.hasNext()) {
                Object key = it.next();
                Object value = ((Map) fieldValue).get(key);
                if (BITypeUtils.isBasicValue(key.getClass())) {
                    result.put("" + key, createFieldJSON(disposedBeans, value));
                } else {
                    result.put(createFieldJSON(disposedBeans, key).toString(), createFieldJSON(disposedBeans, value));
                }
            }
            return result;
        } else {
            return new JSONObject();
        }
    }

    private boolean isDisposed(Map<String, ArrayList<Object>> disposedBeans, Object obj) {
        if (disposedBeans.containsKey(obj.getClass().getName())) {
            Iterator<Object> iterator = disposedBeans.get(obj.getClass().getName()).iterator();
            while (iterator.hasNext()) {
                if (iterator.next() == obj) {
                    return true;
                }
            }
        }
        return false;
    }

    private void setDisposed(Map<String, ArrayList<Object>> disposedBeans, Object obj) {
        if (!disposedBeans.containsKey(obj.getClass().getName())) {
            disposedBeans.put(obj.getClass().getName(), new ArrayList<Object>());
        }
        disposedBeans.get(obj.getClass().getName()).add(obj);

    }

    private Boolean isBasicValue(Field field) {
        return BIFieldUtils.isBasicValue(field);
    }


    private Boolean isBIJsonObject(Object fieldValue) {
        return fieldValue instanceof BIJSONObject;
    }

    private Boolean isIterableObject(Object fieldValue) {
        return BIFieldUtils.isIterableObject(fieldValue);
    }

    private Boolean isMapObject(Object fieldValue) {
        return BIFieldUtils.isMapObject(fieldValue);
    }

    private Boolean isArrayObject(Object fieldValue) {
        return BIFieldUtils.isArrayObject(fieldValue);
    }

    private Boolean isBasicType(Class fieldClass) {
        return BIFieldUtils.isBasicType(fieldClass);
    }

    private Boolean isBIJsonType(Class fieldClass) {
        return BITypeUtils.isAssignable(BIJSONObject.class, fieldClass);
    }

//    public static void main(String[] args) {
//        String.class.isAssignableFrom(Object.class);
//        System.out.println();
//    }

    private Boolean isIterableType(Class fieldClass) {
        return BIFieldUtils.isIterableType(fieldClass);
    }

    private Boolean isMapType(Class fieldClass) {
        return BIFieldUtils.isMapType(fieldClass);
    }

    /**
     * target是否可以赋值给maySuper参数。
     *
     * @param maySuper 变量的类型
     * @param target   实际类型
     * @return
     */
    private Boolean isAssignable(Class maySuper, Class target) {
        return BITypeUtils.isAssignable(maySuper, target);
    }

    private Boolean isArrayType(Class fieldClass) {
        return BIFieldUtils.isArrayType(fieldClass);
    }
//    @Override
//    public void parseJSON(JSONObject jsonObject) throws Exception {
//
//    }
}