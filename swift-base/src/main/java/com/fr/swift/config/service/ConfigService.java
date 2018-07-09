package com.fr.swift.config.service;

import com.fr.third.org.hibernate.criterion.Criterion;

import java.util.List;

/**
 * @author yee
 * @date 2018/7/6
 */
public interface ConfigService<T> {
    List<T> find(Criterion... criterion);
}
