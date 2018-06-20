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
public class GroupByInfoImpl implements GroupByInfo {

    private List<Column> dimensions;
    private DetailFilter detailFilter;
    private List<Sort> sorts;
    private Expander expander;
    private Cursor cursor;

    public GroupByInfoImpl(List<Column> dimensions, DetailFilter detailFilter,
                           List<Sort> sorts, Expander expander, Cursor cursor) {
        this.dimensions = dimensions;
        this.detailFilter = detailFilter;
        this.sorts = sorts;
        this.expander = expander;
        this.cursor = cursor;
    }

    @Override
    public List<Column> getDimensions() {
        return dimensions;
    }

    @Override
    public DetailFilter getDetailFilter() {
        return detailFilter;
    }

    @Override
    public List<Sort> getSorts() {
        return sorts;
    }

    @Override
    public Expander getExpander() {
        return expander;
    }

    @Override
    public Cursor getCursor() {
        return cursor;
    }
}
