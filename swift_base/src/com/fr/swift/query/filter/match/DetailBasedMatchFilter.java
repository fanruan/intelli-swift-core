package com.fr.swift.query.filter.match;

import com.fr.swift.query.filter.detail.DetailFilter;
import com.fr.swift.result.SwiftNode;

/**
 * Created by pony on 2018/4/17.
 */
public class DetailBasedMatchFilter implements MatchFilter{
    private int targetIndex;
    private DetailFilter filter;

    public DetailBasedMatchFilter(int targetIndex, DetailFilter filter) {
        this.targetIndex = targetIndex;
        this.filter = filter;
    }

    @Override
    public boolean matches(SwiftNode node) {
        return filter.matches(node, this.targetIndex);
    }
}
