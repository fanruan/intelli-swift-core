package com.fr.swift.query.info.dimension;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AbstractDimension that = (AbstractDimension) o;

        if (sourceKey != null ? !sourceKey.equals(that.sourceKey) : that.sourceKey != null) return false;
        if (columnKey != null ? !columnKey.equals(that.columnKey) : that.columnKey != null) return false;
        if (group != null ? !group.equals(that.group) : that.group != null) return false;
        if (sort != null ? !sort.equals(that.sort) : that.sort != null) return false;
        return filterInfo != null ? filterInfo.equals(that.filterInfo) : that.filterInfo == null;
    }

    @Override
    public int hashCode() {
        int result = sourceKey != null ? sourceKey.hashCode() : 0;
        result = 31 * result + (columnKey != null ? columnKey.hashCode() : 0);
        result = 31 * result + (group != null ? group.hashCode() : 0);
        result = 31 * result + (sort != null ? sort.hashCode() : 0);
        result = 31 * result + (filterInfo != null ? filterInfo.hashCode() : 0);
        return result;
    }
}
