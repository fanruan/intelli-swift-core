package com.fr.swift.query.filter.match;

import com.fr.swift.result.SwiftNode;

/**
 * Created by pony on 2017/10/9.
 * 处理聚合之后结果的过滤
 */
public interface MatchFilter {
    boolean matches(SwiftNode node);

//    boolean matches(AggregatorValue value);
}
