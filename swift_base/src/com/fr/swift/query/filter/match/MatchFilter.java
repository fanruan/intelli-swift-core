package com.fr.swift.query.filter.match;

import com.fr.swift.result.SwiftNode;

/**
 * Created by pony on 2017/10/9.
 * 处理聚合之后结果的过滤
 */
public interface MatchFilter {
    // 只有对聚合之后的数值进行过滤才是结果过滤，其他都应该是明细过滤。
    boolean matches(SwiftNode node);
}
