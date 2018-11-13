package com.fr.swift.basics.annotation;


import com.fr.swift.basics.ProcessHandler;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 打在代理的方法上
 *
 * @author yee
 * @date 2018/10/23
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface InvokeMethod {
    Class<? extends ProcessHandler> value();

    com.fr.swift.basics.annotation.Target target() default com.fr.swift.basics.annotation.Target.NONE;

}
