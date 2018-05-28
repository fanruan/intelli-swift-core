package com.fr.swift.cal.info;

import com.fr.swift.query.adapter.dimension.Cursor;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AbstractQueryInfo<?> that = (AbstractQueryInfo<?>) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (filterInfo != null ? !filterInfo.equals(that.filterInfo) : that.filterInfo != null) return false;
        return table != null ? table.equals(that.table) : that.table == null;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (filterInfo != null ? filterInfo.hashCode() : 0);
        result = 31 * result + (table != null ? table.hashCode() : 0);
        return result;
    }
}
