package com.fr.swift.config.query.impl;

import com.fr.swift.config.condition.SwiftConfigCondition;
import com.fr.swift.config.oper.ConfigQuery;
import com.fr.swift.config.oper.ConfigSession;
import com.fr.swift.config.oper.ConfigWhere;
import com.fr.swift.config.oper.Order;
import com.fr.swift.config.query.SwiftConfigQuery;

import java.util.Collections;
import java.util.List;

/**
 * @author yee
 * @date 2019-07-30
 */
class NormalQuery<T> implements SwiftConfigQuery<List<T>> {

    private Class<T> tClass;
    private SwiftConfigCondition condition;

    NormalQuery(Class<T> tClass, SwiftConfigCondition condition) {
        this.tClass = tClass;
        this.condition = condition;
    }

    @Override
    public List<T> apply(ConfigSession p) {
        ConfigQuery<T> entityQuery = p.createEntityQuery(tClass);
        entityQuery.where(condition.getWheres().toArray(new ConfigWhere[0]));
        entityQuery.orderBy(condition.getSort().toArray(new Order[0]));
        return Collections.unmodifiableList(entityQuery.executeQuery());
    }
}
