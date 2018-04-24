package com.fr.swift.cal.info;

import com.fr.swift.cal.QueryInfo;
import com.fr.swift.cal.result.group.Cursor;
import com.fr.swift.query.filter.info.FilterInfo;
import com.fr.swift.source.SourceKey;

/**
 * @author pony
 * @date 2017/12/15
 */
public abstract class AbstractQueryInfo<T> implements QueryInfo<T> {
    private Cursor cursor;
    private String id;
    private FilterInfo filterInfo;
    /**
     * query查询的表
     */
    private SourceKey table;

    public AbstractQueryInfo(Cursor cursor, String id, SourceKey table, FilterInfo filterInfo) {
        this.cursor = cursor;
        this.id = id;
        this.table = table;
        this.filterInfo = filterInfo;
    }

    @Override
    public Cursor getCursor() {
        return cursor;
    }

    @Override
    public String getQueryId() {
        return id;
    }

    public SourceKey getTable() {
        return table;
    }

    public FilterInfo getFilterInfo() {
        return filterInfo;
    }

}
