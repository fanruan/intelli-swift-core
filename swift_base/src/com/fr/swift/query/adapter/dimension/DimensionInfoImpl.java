package com.fr.swift.query.adapter.dimension;

import com.fr.swift.query.filter.info.FilterInfo;

/**
 * Created by Lyon on 2018/4/23.
 */
public class DimensionInfoImpl implements DimensionInfo {

    private Cursor cursor;
    private FilterInfo filterInfo;
    private Expander expander;
    private Dimension[] dimensions;

    public DimensionInfoImpl(Cursor cursor, FilterInfo filterInfo, Expander expander, Dimension[] dimensions) {
        this.cursor = cursor;
        this.filterInfo = filterInfo;
        this.expander = expander;
        this.dimensions = dimensions;
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
}
