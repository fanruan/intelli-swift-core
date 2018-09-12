package com.fr.swift.query.filter.info;

import com.fr.swift.query.filter.DetailFilterFactory;
import com.fr.swift.query.filter.SwiftDetailFilterType;
import com.fr.swift.query.filter.detail.DetailFilter;
import com.fr.swift.query.filter.match.DetailBasedMatchFilter;
import com.fr.swift.query.filter.match.MatchFilter;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.source.core.CoreField;

/**
 * @author Lyon
 * @date 2018/2/2
 * 一个过滤器的过滤信息
 */
public class SwiftDetailFilterInfo<T> extends AbstractDetailFilterInfo {

    @CoreField
    private T filterValue;
    @CoreField
    private SwiftDetailFilterType type;

    @CoreField
    private ColumnKey columnKey;

    public SwiftDetailFilterInfo(ColumnKey columnKey, T filterValue, SwiftDetailFilterType type) {
        this.filterValue = filterValue;
        this.type = type;
        this.columnKey = columnKey;
    }


    public T getFilterValue() {
        return filterValue;
    }

    public ColumnKey getColumnKey() {
        return columnKey;
    }

    public SwiftDetailFilterType getType() {
        return type;
    }

    @Override
    public boolean isMatchFilter() {
        return type == SwiftDetailFilterType.BOTTOM_N || type == SwiftDetailFilterType.TOP_N;
    }

    @Override
    public DetailFilter createDetailFilter(Segment segment) {
        return DetailFilterFactory.createFilter(segment, this);
    }

    @Override
    public MatchFilter createMatchFilter() {
        return new DetailBasedMatchFilter(-1, this.createDetailFilter(null));
    }
}
