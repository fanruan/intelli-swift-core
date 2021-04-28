package com.fr.swift.cloud.annotation;

/**
 * This class created on 2019/5/28
 *
 * @author Lucifer
 * @description 用于标记一个类、方法是临时的解决方案，在限定的日期之后会被删除
 */
public @interface Negative {

    /**
     * 修改该问题的最后日期
     *
     * @return 日期，形如2010-05-29
     */
    String until();
}