package com.fr.swift.config.command.impl;

import com.fr.swift.config.command.SwiftConfigCommand;
import com.fr.swift.config.condition.SwiftConfigCondition;
import com.fr.swift.config.oper.ConfigQuery;
import com.fr.swift.config.oper.ConfigSession;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author yee
 * @date 2019-07-30
 */
class DeleteItemCommand<T> implements SwiftConfigCommand<List<T>> {
    private Class<T> tClass;
    private SwiftConfigCondition condition;


    DeleteItemCommand(Class<T> tClass, SwiftConfigCondition condition) {
        this.tClass = tClass;
        this.condition = condition;
    }

    @Override
    public List<T> apply(ConfigSession p) {
        ConfigQuery<T> entityQuery = p.createEntityQuery(tClass);
        // TODO 条件
        entityQuery.where();
        List<T> ts = entityQuery.executeQuery();
        if (ts.isEmpty()) {
            return Collections.emptyList();
        }
        List<T> apply = new ArrayList<>();
        for (T t : ts) {
            p.delete(t);
            apply.add(t);
        }
        return Collections.unmodifiableList(apply);
    }
}
