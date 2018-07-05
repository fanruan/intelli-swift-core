package com.fr.swift.db;

import com.fr.stable.query.condition.QueryCondition;

/**
 * @author anchore
 * @date 2018/3/26
 */
public interface Where {
    //todo 先沿用fr的condition
    QueryCondition getQueryCondition();
}