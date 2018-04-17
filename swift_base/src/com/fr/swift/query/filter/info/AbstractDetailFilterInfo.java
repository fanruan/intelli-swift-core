package com.fr.swift.query.filter.info;

import com.fr.swift.query.filter.match.MatchFilter;

/**
 * Created by pony on 2017/12/21.
 */
public abstract class AbstractDetailFilterInfo extends AbstractFilterInfo{
    @Override
    public boolean isMatchFilter() {
        return false;
    }

    @Override
    public MatchFilter createMatchFilter() {
        return null;
    }
}
