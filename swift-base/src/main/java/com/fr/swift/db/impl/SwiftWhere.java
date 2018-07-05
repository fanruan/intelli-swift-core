package com.fr.swift.db.impl;

import com.fr.stable.query.condition.QueryCondition;
import com.fr.swift.db.Where;

import java.io.Serializable;

/**
 * This class created on 2018/7/4
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
public class SwiftWhere implements Where, Serializable {

    private static final long serialVersionUID = 1116521843669790563L;
    private QueryCondition queryCondition;

    public SwiftWhere(QueryCondition queryCondition) {
        this.queryCondition = queryCondition;
    }

    @Override
    public QueryCondition getQueryCondition() {
        return queryCondition;
    }
}
