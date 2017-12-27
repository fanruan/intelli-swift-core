package com.finebi.base.data.xml.item;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by andrew_asa on 2017/10/9.
 * map类型的对象
 * 目前只支持key值为字符串的map对象
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface XmlMapItem{

    /**
     * key元素
     * @return
     */
    Class keyClass() default String.class;

    /**
     * value元素
     * @return
     */
    Class valueClass() default String.class;

    /**
     * 构造对象
     * @return
     */
    Class valueFactory() default String.class;
}
