package com.fr.swift.base.json.serialization;

import com.fr.swift.base.json.annotation.JsonSubTypes;
import com.fr.swift.base.json.annotation.JsonTypeInfo;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.Set;

/**
 * @author yee
 * @date 2018-12-12
 */
public interface SerializationConfig extends Cloneable {
    /**
     * 获取忽略的字段
     *
     * @return
     */
    Set<String> ignoreProperties();


    /**
     * 添加忽略字段
     *
     * @param ignoreProperties
     */
    void addIgnore(String... ignoreProperties);

    /**
     * 设置指定字段作为实体类区分，对应
     *
     * @return
     * @see JsonSubTypes.Type#name()
     * @see JsonTypeInfo#property()
     */
    String getPropertyName();

    /**
     * 设置区分字段
     *
     * @param propertyName
     */
    void setPropertyName(String propertyName);


    /**
     * 对应通过Type的name取得相应的实体类
     *
     * @param name
     * @return
     * @see JsonSubTypes.Type#name()
     * @see JsonSubTypes.Type#value()
     */
    Class instanceClass(String name);

    /**
     * 该接口的默认实现类
     *
     * @return
     * @see JsonTypeInfo#defaultImpl()
     */
    Class defaultImpl();

    /**
     * 设置默认实现类
     *
     * @param defaultImpl
     */
    void setDefaultImpl(Class defaultImpl);

    /**
     * 添加实体类对应关系
     *
     * @param name
     * @param instanceClass
     * @see JsonSubTypes.Type#name()
     * @see JsonSubTypes.Type#value()
     */
    void addInstanceMap(String name, Class instanceClass);

    /**
     * 获取所有实体类对应关系
     *
     * @return
     */
    Map<String, Class> getInstanceMap();

    /**
     * 所有需要序列化属性的获取器
     *
     * @return
     */
    Map<String, BeanGetter> getters();

    /**
     * 所有需要反序列化属性的设值器
     *
     * @return
     */
    Map<String, BeanSetter> setters();

    /**
     * 设置所有需要反序列化属性的设值器
     *
     * @param getters
     * @return
     */
    void setGetters(Map<String, BeanGetter> getters);

    /**
     * 设置所有需要反序列化属性的设值器
     *
     * @param setters
     * @return
     */
    void setSetters(Map<String, BeanSetter> setters);

    /**
     * 拷贝方法
     *
     * @return
     */
    SerializationConfig copy();

    /**
     * 序列化值获取器
     */
    interface BeanGetter {
        /**
         * 获取属性的值
         *
         * @param o
         * @return
         * @throws Exception
         */
        Object get(Object o) throws Exception;
    }

    /**
     * 反序列化值设置器
     */
    interface BeanSetter {
        /**
         * 设置属性值
         *
         * @param object
         * @param value
         * @throws Exception
         */
        void set(Object object, Object value) throws Exception;

        /**
         * 属性泛型类型
         *
         * @return
         */
        Class<?> genericType();

        Class<?> genericType(Type clazz);

        /**
         * 属性类型
         *
         * @return
         */
        Class<?> propertyType();

        Class<?> propertyType(Type clazz);

        String getField();
    }
}

