package com.fr.swift.cloud.query.filter.match;

import com.fr.swift.cloud.result.SwiftNode;

/**
 * Created by pony on 2017/10/9.
 * 处理聚合之后结果的过滤
 */
public interface MatchFilter {
    MatchFilter TRUE = new MatchFilter() {
        @Override
        public boolean matches(SwiftNode node) {
            return true;
        }
    };

    /**
     * 结果过滤，可能包装了明细过滤器
     *
     * @param node
     * @return
     */
    boolean matches(SwiftNode node);
}
