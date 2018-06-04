package com.fr.swift.query.group.info;

import com.fr.swift.query.filter.detail.DetailFilter;
import com.fr.swift.query.group.info.cursor.Cursor;
import com.fr.swift.query.group.info.cursor.Expander;
import com.fr.swift.query.sort.Sort;
import com.fr.swift.segment.column.Column;

import java.util.List;

/**
 * Created by Lyon on 2018/4/25.
 */
public class PageGroupInfoImpl extends GroupByInfoImpl implements PageGroupByInfo {

    private Cursor cursor;

    public PageGroupInfoImpl(List<Column> dimensions, DetailFilter detailFilter, List<Sort> sorts, Expander expander, Cursor cursor) {
        super(dimensions, detailFilter, sorts, expander);
    }

    @Override
    public Cursor getCursor() {
        return cursor;
    }
}
