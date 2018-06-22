package com.fr.swift.query.info.bean.factory;

/**
 * @author yee
 * @date 2018/6/22
 */
public interface BeanFactory<S, T> {
    T create(S source);
}
