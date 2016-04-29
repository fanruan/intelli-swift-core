package com.fr.bi.common.factory.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p/>
 * 当前Class注册到Factory，每次获取实例都是同一个
 * <p/>
 * Created by Connery on 2015/12/7.
 */

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface BISingletonObject {
}