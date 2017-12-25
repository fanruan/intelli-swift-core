package com.finebi.base.data.xml.item;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author andrew.asa
 * @create 2017-10-21
 * list类型
 **/
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface XmlListItem {

    /**
     * 元素类型
     *
     * @return
     */
    Class clazz() default String.class;

    /**
     * list里面的元素是否为接口
     *
     * @return
     */
    boolean isInterface() default false;


    /**
     * 构造对象
     *
     * @return
     */
    Class constructObject() default String.class;
}
