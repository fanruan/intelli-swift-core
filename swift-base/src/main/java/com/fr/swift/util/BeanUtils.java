package com.fr.swift.util;

import com.fr.swift.exception.FactoryKeyDuplicateException;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * java bean的基本操作
 * <p/>
 * <p/>
 * Created by Connery on 2015/12/18.
 */
public class BeanUtils {

    public static ArrayList<Method> fetchPrefixMethod(Class clazz, String prefix) {
        ArrayList<Method> result = new ArrayList<Method>();
        Method[] methods = clazz.getDeclaredMethods();
        for (int i = 0; i < methods.length; i++) {
            Method method = methods[i];
            if (method.getName().startsWith(prefix)) {
                result.add(method);
            }
        }
        return result;
    }

    /**
     * 获得Set的方法
     *
     * @param clazz
     * @return
     * @throws FactoryKeyDuplicateException
     */
    public static ArrayList<Method> fetchSetterMethod(Class clazz) throws FactoryKeyDuplicateException {
        return filterMethodByParaAmount(fetchAttributeMethod(clazz, "set"), 1);
    }

    public static ArrayList<Method> fetchAttributeMethod(Class clazz, String prefix) throws FactoryKeyDuplicateException {
        ArrayList<Method> result = new ArrayList<Method>();
        if (clazz != null) {
            ArrayList<Method> methods = fetchPrefixMethod(clazz, prefix);
            Iterator<Method> it = methods.iterator();
            Map<String, Method> nameMethod = new HashMap<String, Method>();
            while (it.hasNext()) {
                Method method = it.next();

                String name = method.getName().substring(prefix.length());
                if (nameMethod.containsKey(name.toLowerCase())) {
                    throw new FactoryKeyDuplicateException("Please check the Class:" + clazz.getName() + " and it's Method:" + method.getName() + ".\nThere" +
                            "maybe two methods or two attributes they are same literal name");
                } else {
                    nameMethod.put(name.toLowerCase(), method);
                }
            }
            Field[] fields = clazz.getDeclaredFields();
            for (int i = 0; i < fields.length; i++) {
                Field field = fields[i];
                if (nameMethod.containsKey(field.getName().toLowerCase())) {
                    result.add(nameMethod.get(field.getName().toLowerCase()));
                }
            }
        }
        return result;

    }

    /**
     * 获得Set的方法
     * ToDo 布尔型没有判断，而且对于参数类型缺少判断
     *
     * @throws FactoryKeyDuplicateException
     */
    public static ArrayList<Method> fetchGetterMethod(Class clazz) throws FactoryKeyDuplicateException {

        return filterMethodByParaAmount(fetchAttributeMethod(clazz, "get"), 0);
    }

    /**
     * 根据参数个数过滤方法
     *
     * @param methods
     * @param paraAmountLimit
     * @return
     */
    public static ArrayList<Method> filterMethodByParaAmount(ArrayList<Method> methods, int paraAmountLimit) {

        ArrayList<Method> result = new ArrayList<Method>();
        if (methods != null) {
            Iterator<Method> it = methods.iterator();
            while (it.hasNext()) {
                Method method = it.next();
                if (method.getParameterTypes().length == paraAmountLimit) {
                    result.add(method);
                }
            }
        }
        return result;
    }

    public static ArrayList<Field> fetchAttributes(Class clazz) {
        return fetchAttributes(clazz, new ArrayList<Field>());
    }

    public static ArrayList<Field> fetchAttributes(Class clazz, ArrayList<Field> result) {
        if (clazz != null) {
            Field[] fields = clazz.getDeclaredFields();
            for (int i = 0; i < fields.length; i++) {
                result.add(fields[i]);
            }
        }
        return result;
    }

    /**
     * 获得包括父类的所有属性
     *
     * @param clazz
     * @return
     */
    public static ArrayList<Field> fetchAllAttributes(Class clazz, ArrayList<Field> result, Class limit) {
        if (Util.equals(clazz, limit)) {
            return result;
        } else {
            if (clazz.getSuperclass() == null) {
                return result;
            } else {
                return fetchAllAttributes(clazz.getSuperclass(), fetchAttributes(clazz, result), limit);
            }
        }
    }


    /**
     * 获得包括父类的所有属性
     *
     * @param clazz
     * @return
     */
    public static ArrayList<Field> fetchAllAttributes(Class clazz) {
        return fetchAllAttributes(clazz, new ArrayList<Field>(), Object.class);
    }

    /**
     * 获得包括父类的所有属性
     *
     * @param clazz
     * @return
     */
    public static ArrayList<Field> fetchAllAttributes(Class clazz, Class limit) {
        return fetchAllAttributes(clazz, new ArrayList<Field>(), limit);
    }
}