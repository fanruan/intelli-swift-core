package com.finebi.base.data.xml.item;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by andrew_asa on 2017/10/23.
 * 对象
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface XmlObjectItem {

    /**
     * 工厂类
     * @return
     */
    Class factory() default String.class;

    /**
     * 是否扫描其父类的字段
     * @return
     */
    boolean deepScran() default false;
}
