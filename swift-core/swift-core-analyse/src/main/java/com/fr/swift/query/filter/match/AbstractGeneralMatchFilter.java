package com.fr.swift.query.filter.match;

import java.util.List;

/**
 * Created by pony on 2018/4/17.
 */
public abstract class AbstractGeneralMatchFilter implements MatchFilter {
    protected List<MatchFilter> children;

    public AbstractGeneralMatchFilter(List<MatchFilter> children) {
        this.children = children;
    }
}
