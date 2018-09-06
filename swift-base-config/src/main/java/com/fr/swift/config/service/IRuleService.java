package com.fr.swift.config.service;

/**
 * @author yee
 * @date 2018/7/16
 */
public interface IRuleService<T> {
    T getCurrentRule();

    void setCurrentRule(T rule);

    void setCurrentRule(String className);

    void setCurrentRule(Class<? extends T> clazz);
}
