package com.fr.swift.config.oper;

import java.util.List;

/**
 * @author yee
 * @date 2018-11-27
 */
public interface ConfigCriteria {
    void addOrder(Object order);

    void add(Object criterion);

    <T> List<T> list();
}
