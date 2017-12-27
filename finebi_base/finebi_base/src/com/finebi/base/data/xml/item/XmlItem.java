package com.finebi.base.data.xml.item;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by andrew_asa on 2017/9/28.
 * 基本数据类型
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface XmlItem {

    /**
     * 元素是否为接口
     * @return
     */
    boolean isInterface() default false;

    /**
     * 构造对象
     * @return
     */
    Class objectFactory() default String.class;
}
