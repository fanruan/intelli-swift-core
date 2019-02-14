package com.fr.swift.query.info;

import com.fr.swift.query.filter.info.FilterInfo;
import com.fr.swift.query.info.element.dimension.Dimension;
import com.fr.swift.result.qrs.QueryResultSet;
import com.fr.swift.source.SourceKey;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author pony
 * @date 2017/12/15
 */
public abstract class AbstractQueryInfo<T extends QueryResultSet> implements SingleTableQueryInfo<T> {

    private String id;
    private int fetchSize;
    private FilterInfo filterInfo;
    private SourceKey table;
    private List<Dimension> dimensions;
    private Set<String> queryTarget = new HashSet<String>();

    public AbstractQueryInfo(String id, int fetchSize, SourceKey table, FilterInfo filterInfo, List<Dimension> dimensions) {
        this.id = id;
        this.fetchSize = fetchSize;
        this.table = table;
        this.filterInfo = filterInfo;
        this.dimensions = dimensions;
    }

    @Override
    public String getQueryId() {
        return id;
    }

    @Override
    public int getFetchSize() {
        return fetchSize;
    }

    @Override
    public SourceKey getTable() {
        return table;
    }

    @Override
    public FilterInfo getFilterInfo() {
        return filterInfo;
    }

    @Override
    public List<Dimension> getDimensions() {
        return dimensions;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        AbstractQueryInfo<?> that = (AbstractQueryInfo<?>) o;

        if (id != null ? !id.equals(that.id) : that.id != null) {
            return false;
        }
        if (filterInfo != null ? !filterInfo.equals(that.filterInfo) : that.filterInfo != null) {
            return false;
        }
        if (table != null ? !table.equals(that.table) : that.table != null) {
            return false;
        }
        return dimensions != null ? dimensions.equals(that.dimensions) : that.dimensions == null;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (filterInfo != null ? filterInfo.hashCode() : 0);
        result = 31 * result + (table != null ? table.hashCode() : 0);
        result = 31 * result + (dimensions != null ? dimensions.hashCode() : 0);
        return result;
    }

    @Override
    public Set<String> getQuerySegment() {
        return queryTarget;
    }

    @Override
    public void setQuerySegment(Set<String> queryTarget) {
        this.queryTarget = queryTarget;
    }
}
