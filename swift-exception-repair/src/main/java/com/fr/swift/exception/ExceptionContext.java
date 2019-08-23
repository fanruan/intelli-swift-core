package com.fr.swift.exception;

/**
 * @author anchore
 * @date 2019/8/15
 * <p>
 * 异常上下文
 * <p>
 * 粒度最好细到单个元素，好做id
 * 也是immut的
 */
public interface ExceptionContext<T> {
    @Deprecated
    T getContext();

    @Deprecated
    @Override
    boolean equals(Object o);

    @Deprecated
    @Override
    int hashCode();
}