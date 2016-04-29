package com.fr.bi.common.persistent.json.analyser.anno;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 指定字段的实现，可能是具体的子类
 * 或者是相应的工厂类
 * Created by Connery on 2016/1/21.
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface BIElementType {
    /**
     * 当前的字段用哪个子类实现
     *
     * @return
     */
    Class implemented() default Object.class;

    Class genericType() default Object.class;
}