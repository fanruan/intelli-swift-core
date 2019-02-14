package com.fr.swift.query.group.info;

import com.fr.swift.query.filter.detail.DetailFilter;
import com.fr.swift.query.group.info.cursor.Cursor;
import com.fr.swift.query.sort.Sort;
import com.fr.swift.segment.column.Column;
import com.fr.swift.structure.Pair;

import java.util.List;

/**
 * Created by Lyon on 2018/4/25.
 */
public class GroupByInfoImpl implements GroupByInfo {

    private int fetchSize;
    private List<Pair<Column, IndexInfo>> dimensions;
    private DetailFilter detailFilter;
    private List<Sort> sorts;
    private Cursor cursor;

    public GroupByInfoImpl(int fetchSize, List<Pair<Column, IndexInfo>> dimensions, DetailFilter detailFilter, List<Sort> sorts, Cursor cursor) {
        this.fetchSize = fetchSize;
        this.dimensions = dimensions;
        this.detailFilter = detailFilter;
        this.sorts = sorts;
        this.cursor = cursor;
    }

    @Override
    public int getFetchSize() {
        return fetchSize;
    }

    @Override
    public List<Pair<Column, IndexInfo>> getDimensions() {
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
    public Cursor getCursor() {
        return cursor;
    }
}
