package com.fr.swift.query.adapter.metric;

import com.fr.swift.query.adapter.AbstractQueryColumn;
import com.fr.swift.query.filter.info.FilterInfo;
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
}
