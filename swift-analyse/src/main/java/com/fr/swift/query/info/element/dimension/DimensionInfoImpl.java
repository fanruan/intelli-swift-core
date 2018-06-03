package com.fr.swift.query.info.element.dimension;

import com.fr.swift.query.filter.info.FilterInfo;
import com.fr.swift.query.group.info.cursor.Cursor;
import com.fr.swift.query.group.info.cursor.Expander;
import com.fr.swift.query.group.info.cursor.ExpanderImpl;
import com.fr.swift.query.group.info.cursor.ExpanderType;
import com.fr.swift.result.row.RowIndexKey;

import java.util.Arrays;
import java.util.HashSet;

/**
 * Created by Lyon on 2018/4/23.
 */
public class DimensionInfoImpl implements DimensionInfo {

    private boolean isShowSum = true;
    private Cursor cursor;
    private FilterInfo filterInfo;
    private Expander expander;
    private Dimension[] dimensions;

    public DimensionInfoImpl(Cursor cursor, FilterInfo filterInfo, Expander expander, Dimension[] dimensions) {
        this.cursor = cursor;
        this.filterInfo = filterInfo;
        this.expander = expander == null ?
                new ExpanderImpl(ExpanderType.ALL_EXPANDER, new HashSet<RowIndexKey<String[]>>()) : expander;
        this.dimensions = dimensions;
    }

    public DimensionInfoImpl(boolean isShowSum, Cursor cursor, FilterInfo filterInfo, Expander expander, Dimension[] dimensions) {
        this(cursor, filterInfo, expander, dimensions);
        this.isShowSum = isShowSum;
    }

    @Override
    public Dimension[] getDimensions() {
        return dimensions;
    }

    @Override
    public FilterInfo getFilterInfo() {
        return filterInfo;
    }

    @Override
    public Cursor getCursor() {
        return cursor;
    }

    @Override
    public Expander getExpander() {
        return expander;
    }

    @Override
    public boolean isShowSum() {
        return isShowSum;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DimensionInfoImpl that = (DimensionInfoImpl) o;

        if (isShowSum != that.isShowSum) return false;
        if (filterInfo != null ? !filterInfo.equals(that.filterInfo) : that.filterInfo != null) return false;
        if (expander != null ? !expander.equals(that.expander) : that.expander != null) return false;
        // Probably incorrect - comparing Object[] arrays with Arrays.equals
        return Arrays.equals(dimensions, that.dimensions);
    }

    @Override
    public int hashCode() {
        int result = (isShowSum ? 1 : 0);
        result = 31 * result + (filterInfo != null ? filterInfo.hashCode() : 0);
        result = 31 * result + (expander != null ? expander.hashCode() : 0);
        result = 31 * result + Arrays.hashCode(dimensions);
        return result;
    }
}
