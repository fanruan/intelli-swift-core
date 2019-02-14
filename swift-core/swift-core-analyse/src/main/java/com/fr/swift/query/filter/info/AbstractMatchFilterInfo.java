package com.fr.swift.query.filter.info;

/**
 * Created by pony on 2017/12/21.
 */
public abstract class AbstractMatchFilterInfo extends AbstractFilterInfo {
    @Override
    public boolean isMatchFilter() {
        return true;
    }
}
