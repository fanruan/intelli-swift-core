package com.fr.swift.config.query.impl;

import com.fr.swift.config.oper.ConfigSession;
import com.fr.swift.config.query.SwiftConfigQuery;

import java.io.Serializable;

/**
 * @author yee
 * @date 2019-07-30
 */
class IdQuery<T> implements SwiftConfigQuery<T> {
    private Class<T> tClass;
    private Serializable id;

    IdQuery(Class<T> tClass, Serializable id) {
        this.tClass = tClass;
        this.id = id;
    }

    @Override
    public T apply(ConfigSession p) {
        return p.get(tClass, id);
    }
}
