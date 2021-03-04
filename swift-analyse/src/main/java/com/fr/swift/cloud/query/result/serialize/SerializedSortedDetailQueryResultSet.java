package com.fr.swift.cloud.query.result.serialize;

import com.fr.swift.cloud.source.Row;

import java.util.Comparator;
import java.util.List;

/**
 * @author lyon
 * @date 2018/12/29
 */
public class SerializedSortedDetailQueryResultSet extends SerializedDetailQueryResultSet {
    private static final long serialVersionUID = 7157539239974647191L;
    private final Comparator<Row> comparator;

    public SerializedSortedDetailQueryResultSet(int fetchSize, int rowCount, Comparator<Row> comparator, List<Row> page, boolean hasNextPage) {
        super(fetchSize, rowCount, page, hasNextPage);
        this.comparator = comparator;
    }

    public Comparator<Row> getComparator() {
        return comparator;
    }
}
