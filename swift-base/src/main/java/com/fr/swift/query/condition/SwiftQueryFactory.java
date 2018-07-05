package com.fr.swift.query.condition;

import com.fr.stable.query.condition.QueryCondition;

/**
 * This class created on 2018/7/4
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
public class SwiftQueryFactory {
    public SwiftQueryFactory() {
    }

    public static QueryCondition create() {
        return new SwiftQueryCondition();
    }
}
