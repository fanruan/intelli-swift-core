package com.fr.swift.config.command.impl;

import com.fr.swift.config.command.SwiftConfigCommand;
import com.fr.swift.config.condition.SwiftConfigCondition;
import com.fr.swift.config.oper.ConfigDelete;
import com.fr.swift.config.oper.ConfigSession;
import com.fr.swift.config.oper.ConfigWhere;

/**
 * @author yee
 * @date 2019-07-30
 */
class DeleteItemCommand<T> implements SwiftConfigCommand<Integer> {
    private Class<T> tClass;
    private SwiftConfigCondition condition;


    DeleteItemCommand(Class<T> tClass, SwiftConfigCondition condition) {
        this.tClass = tClass;
        this.condition = condition;
    }

    @Override
    public Integer apply(ConfigSession p) {
        ConfigDelete<T> entityQuery = p.createEntityDelete(tClass);
        entityQuery.where(condition.getWheres().toArray(new ConfigWhere[0]));
        return entityQuery.delete();
    }
}
