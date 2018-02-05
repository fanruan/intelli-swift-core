package com.fr.swift.cal.info;

import com.fr.swift.cal.QueryInfo;
import com.fr.swift.cal.result.group.Cursor;
import com.fr.swift.query.filter.info.FilterInfo;

/**
 * Created by pony on 2017/12/15.
 */
public abstract class AbstractQueryInfo<T> implements QueryInfo<T> {
    private Cursor cursor;
    private String id;
    private FilterInfo filterInfo;

    public AbstractQueryInfo(Cursor cursor, String id, FilterInfo filterInfo) {
        this.cursor = cursor;
        this.id = id;
        this.filterInfo = filterInfo;
    }

    @Override
    public Cursor getCursor() {
        return cursor;
    }

    public String getQueryID() {
        return id;
    }

    public FilterInfo getFilterInfo() {
        return filterInfo;
    }

}
