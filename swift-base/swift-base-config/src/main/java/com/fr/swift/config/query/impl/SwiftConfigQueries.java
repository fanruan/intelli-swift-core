package com.fr.swift.config.query.impl;

import com.fr.swift.config.condition.SwiftConfigCondition;
import com.fr.swift.config.oper.Page;
import com.fr.swift.config.query.SwiftConfigQuery;

import java.io.Serializable;
import java.util.List;

/**
 * @author yee
 * @date 2019-07-30
 */
public class SwiftConfigQueries {
    public static <T> SwiftConfigQuery<T> of(Class<T> tClass, Serializable id) {
        return new IdQuery<>(tClass, id);
    }

    public static <T> SwiftConfigQuery<Page<T>> of(Class<T> tClass, int page, int size, SwiftConfigCondition condition) {
        return new PageQuery<>(tClass, page, size, condition);
    }


    public static <T> SwiftConfigQuery<List<T>> of(Class<T> tClass, SwiftConfigCondition condition) {
        return new NormalQuery<>(tClass, condition);
    }
}
