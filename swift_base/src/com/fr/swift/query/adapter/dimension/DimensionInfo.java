package com.fr.swift.query.adapter.dimension;

import com.fr.swift.query.filter.info.FilterInfo;

/**
 * Created by Lyon on 2018/4/23.
 */
public interface DimensionInfo {

    Dimension[] getDimensions();

    FilterInfo getFilterInfo();

    Cursor getCursor();

    Expander getExpander();
}
