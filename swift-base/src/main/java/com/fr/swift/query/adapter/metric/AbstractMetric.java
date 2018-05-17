package com.fr.swift.query.adapter.metric;

import com.fr.swift.query.adapter.AbstractQueryColumn;
import com.fr.swift.query.filter.info.FilterInfo;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.column.Column;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.source.SourceKey;

/**
 * Created by pony on 2017/12/21.
 */
public abstract class AbstractMetric extends AbstractQueryColumn implements Metric{

    private SourceKey sourceKey;
    private ColumnKey columnKey;
    private FilterInfo filterInfo;

    public AbstractMetric(int index, SourceKey sourceKey, ColumnKey columnKey, FilterInfo filterInfo) {
        super(index);
        this.sourceKey = sourceKey;
        this.columnKey = columnKey;
        this.filterInfo = filterInfo;
    }

    @Override
    public FilterInfo getFilter() {
        return filterInfo;
    }

    @Override
    public SourceKey getSourceKey() {
        return sourceKey;
    }

    @Override
    public ColumnKey getColumnKey() {
        return columnKey;
    }

    @Override
    public Column getColumn(Segment segment) {
        return segment.getColumn(getColumnKey());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AbstractMetric that = (AbstractMetric) o;

        if (sourceKey != null ? !sourceKey.equals(that.sourceKey) : that.sourceKey != null) return false;
        if (columnKey != null ? !columnKey.equals(that.columnKey) : that.columnKey != null) return false;
        return filterInfo != null ? filterInfo.equals(that.filterInfo) : that.filterInfo == null;
    }

    @Override
    public int hashCode() {
        int result = sourceKey != null ? sourceKey.hashCode() : 0;
        result = 31 * result + (columnKey != null ? columnKey.hashCode() : 0);
        result = 31 * result + (filterInfo != null ? filterInfo.hashCode() : 0);
        return result;
    }
}
