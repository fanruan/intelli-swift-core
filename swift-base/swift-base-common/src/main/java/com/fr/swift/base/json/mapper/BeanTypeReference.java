package com.fr.swift.base.json.mapper;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * @author yee
 * @date 2018-11-27
 */
public abstract class BeanTypeReference<T> implements Comparable<BeanTypeReference<T>> {
    private final Type type;

    protected BeanTypeReference() {
        Type superClass = this.getClass().getGenericSuperclass();
        if (superClass instanceof Class) {
            throw new IllegalArgumentException("Internal error: TypeReference constructed without actual type information");
        } else {
            this.type = ((ParameterizedType) superClass).getActualTypeArguments()[0];
        }
    }

    public Type getType() {
        return this.type;
    }

    @Override
    public int compareTo(BeanTypeReference<T> o) {
        return 0;
    }
}
