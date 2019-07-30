package com.fr.swift.config.query.impl;

import com.fr.swift.config.condition.SwiftConfigCondition;
import com.fr.swift.config.oper.ConfigQuery;
import com.fr.swift.config.oper.ConfigSession;
import com.fr.swift.config.oper.Page;
import com.fr.swift.config.query.SwiftConfigQuery;

/**
 * @author yee
 * @date 2019-07-30
 */
class PageQuery<T> implements SwiftConfigQuery<Page<T>> {
    private Class<T> tClass;
    private int page;
    private int size;
    private SwiftConfigCondition condition;

    PageQuery(Class<T> tClass, int page, int size, SwiftConfigCondition condition) {
        this.tClass = tClass;
        this.page = page;
        this.size = size;
        this.condition = condition;
    }


    @Override
    public Page<T> apply(ConfigSession p) {
        ConfigQuery<T> entityQuery = p.createEntityQuery(tClass);
        // TODO 条件
        entityQuery.where();
        return entityQuery.executeQuery(page, size);
    }
}
