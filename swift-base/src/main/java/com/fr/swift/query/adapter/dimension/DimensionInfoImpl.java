package com.fr.swift.query.adapter.dimension;

import com.fr.swift.query.filter.info.FilterInfo;
import com.fr.swift.result.row.RowIndexKey;

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
}
