package com.fr.swift.query.adapter.dimension;

import com.fr.swift.query.adapter.AbstractQueryColumn;
import com.fr.swift.query.filter.info.FilterInfo;
import com.fr.swift.query.group.Group;
import com.fr.swift.query.sort.Sort;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.column.Column;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.source.SourceKey;

/**
 * Created by pony on 2017/12/21.
 */
public abstract class AbstractDimension extends AbstractQueryColumn implements Dimension{

    private SourceKey sourceKey;
    private ColumnKey columnKey;
    private Group group;
    private Sort sort;
    private FilterInfo filterInfo;

    public AbstractDimension(int index, SourceKey sourceKey, ColumnKey columnKey, Group group, Sort sort, FilterInfo filterInfo) {
        super(index);
        this.sourceKey = sourceKey;
        this.columnKey = columnKey;
        this.group = group;
        this.sort = sort;
        this.filterInfo = filterInfo;
    }

    @Override
    public Sort getSort() {
        return sort;
    }

    @Override
    public Group getGroup() {
        return group;
    }

    @Override
    public FilterInfo getFilter() {
        return filterInfo;
    }

    @Override
    public SourceKey getSourceKey() {
        return sourceKey;
    }

    public ColumnKey getColumnKey() {
        return columnKey;
    }

    @Override
    public Column getColumn(Segment segment) {
        return segment.getColumn(columnKey);
    }
}
