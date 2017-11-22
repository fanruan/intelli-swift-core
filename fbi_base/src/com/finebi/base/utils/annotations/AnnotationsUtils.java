package com.finebi.base.utils.annotations;

import com.finebi.base.constant.XMLConstant;
import com.finebi.base.exception.MethodMissException;
import com.finebi.base.utils.data.bool.BooleanUtils;
import com.fr.stable.StringUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by andrew_asa on 2017/9/29.
 */
public class AnnotationsUtils {

    /**
     * 反射方法
     *
     * @param o
     * @param methodName
     * @param args
     * @return
     * @throws Exception
     */
    public static Object invokeMethod(Object o, String methodName, Object... args) throws Exception {

        Class[] parameter = new Class[args.length];
        for (int i = 0; i < args.length; i++) {
            parameter[i] = args[i].getClass();
        }
        Method method = o.getClass().getDeclaredMethod(methodName, parameter);
        method.invoke(o, args);
        return o;
    }

    /**
     * 根据类型反射方法
     *
     * @param dst
     * @param methodName
     * @param paramsType
     * @param params
     * @return
     * @throws Exception
     */
    public static Object invokeMethodByType(Object dst, String methodName, Class[] paramsType, Object[] params) throws Exception {

        Method method = dst.getClass().getDeclaredMethod(methodName, paramsType);
        method.invoke(dst, params);
        return dst;
    }

    /**
     * 反射对象的set方法
     *
     * @param o     反射对象
     * @param field 字段
     * @param param 参数
     * @return
     * @throws Exception
     */
    public static Object invokeSetMethod(Object o, Field field, String param) throws Exception {

        String fieldName = field.getName();
        Class ft = field.getType();
        Method method = o.getClass().getDeclaredMethod(getSetMethodName(fieldName), ft);
        method.invoke(o, getBaseObjectFromStr(ft, param));
        return o;
    }

    /**
     * 反射set方法
     *
     * @param o
     * @param fieldName
     * @param param
     * @param ft
     * @throws Exception
     */
    public static void invokeSetMethodByFieldName(Object o, String fieldName, String param, Class ft) throws Exception {

        String setFieldMethodName = getSetMethodName(fieldName);
        for (Method method : o.getClass().getMethods()) {
            if (method.getName().equals(setFieldMethodName)) {
                method.invoke(o, getBaseObjectFromStr(ft, param));
            }
        }
    }

    public static Object getBaseObjectFromStr(Class ft, String param) {

        String fts = ft.toString();
        if (XMLConstant.TYPE.INT.equals(fts)) {
            return Integer.parseInt(param);
        } else if (XMLConstant.TYPE.LONG.equals(fts)) {
            return Long.parseLong(param);
        } else if (XMLConstant.TYPE.DOUBLE.equals(fts)) {
            return Double.parseDouble(param);
        } else if (XMLConstant.TYPE.BOOLEAN.equals(fts)) {
            return BooleanUtils.toBoolean(param);
        }
        return param;
    }


    public static String getSetMethodName(String fieldName) {

        return "set" + upperFirstChart(fieldName);
    }

    /**
     * @param object
     * @param fieldName
     * @param param
     * @return
     * @throws Exception
     */
    public static Object invokeSetMethod(Object object, String fieldName, String param) throws Exception {

        for (Field field : object.getClass().getDeclaredFields()) {
            if (fieldName.equals(field.getName())) {
                invokeSetMethod(object, field, param);
            }
        }
        return object;
    }

    public static <T> T invokeGetMethod(Object object, Field field) throws Exception {

        String fieldName = field.getName();
        Method method = object.getClass().getDeclaredMethod(fieldNameToGetMethodGetName(fieldName));
        Object ret = method.invoke(object);
        return (T) ret;
    }

    /**
     * 获取原始值
     *
     * @param object
     * @param field
     * @return
     * @throws Exception
     */
    public static Object getOriginalValue(Object object, Field field) throws Exception {

        field.setAccessible(true);
        return field.get(object);
    }

    /**
     * 设置原始值
     *
     * @param object
     * @param field
     * @return
     * @throws Exception
     */
    public static void setOriginalValue(Object object, Field field, Object value) throws Exception {

        field.setAccessible(true);
        field.set(object, value);
    }


    private static String fieldNameToGetMethodGetName(String fieldName) {

        return "get" + upperFirstChart(fieldName);
    }

    /**
     * 没有参数的get方法
     *
     * @param object
     * @param fieldName
     * @param <T>
     * @return
     * @throws Exception
     */
    public static <T> T invokeGetMethod(Object object, String fieldName) throws Exception {

        Method method = object.getClass().getMethod(fieldNameToGetMethodGetName(fieldName));
        Object ret = method.invoke(object);
        return (T) ret;
    }

    /**
     * todo 继承的情况下
     *
     * @param object
     * @param fieldName
     * @param <T>
     * @return
     * @throws Exception
     */
    public static <T> T invokeGetMethodByFieldName(Object object, String fieldName) throws Exception {

        String methodName = fieldNameToGetMethodGetName(fieldName);
        for (Method method : object.getClass().getMethods()) {
            if (method.getName().equals(methodName)) {
                return (T) method.invoke(object);
            }
        }
        throw new MethodMissException(object.toString() + " method miss field:" + methodName);
    }


    /**
     * 第一个字母大写
     *
     * @param str
     * @return
     */
    public static String upperFirstChart(String str) {

        if (StringUtils.isEmpty(str)) {
            return str;
        }
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    /**
     * 调用带有某个注解的字段
     *
     * @param clazz
     * @param annotationClass
     * @param <T>
     * @return
     * @throws Exception
     */
    public static <T extends Annotation> Map<Field, T> getAnnotationFields(Class clazz, Class<T> annotationClass) throws Exception {

        return getAnnotationFields(clazz, annotationClass, false);
    }

    public static <T extends Annotation> Map<Field, T> getAnnotationFields(Class clazz, Class<T> annotationClass, boolean deep) throws Exception {

        Map<Field, T> ret = new HashMap<Field, T>();
        for (Field field : clazz.getDeclaredFields()) {
            Annotation a = field.getAnnotation(annotationClass);
            if (a != null) {
                ret.put(field, (T) a);
            }
        }
        // 如果是深度扫描且当前class不为Object类型
        if (deep && !clazz.getSuperclass().equals(Object.class)) {
            Class sc = clazz.getSuperclass();
            Map<Field, T> cf = getAnnotationFields(sc, annotationClass, deep);
            ret.putAll(cf);
        }
        return ret;
    }
}
