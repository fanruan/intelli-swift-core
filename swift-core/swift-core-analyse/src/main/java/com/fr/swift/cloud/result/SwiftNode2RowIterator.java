package com.fr.swift.cloud.result;

import com.fr.swift.cloud.result.qrs.QueryResultSet;
import com.fr.swift.cloud.source.Row;
import com.fr.swift.cloud.structure.iterator.IteratorUtils;
import com.fr.swift.cloud.util.IoUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Lyon
 * @date 2018/6/13
 */
public class SwiftNode2RowIterator implements SwiftRowIterator {

    private QueryResultSet<SwiftNode> source;
    private List<Row> rows;
    private int cursor = 0;

    public SwiftNode2RowIterator(QueryResultSet<SwiftNode> source) {
        this.source = source;
        this.rows = source.hasNextPage() ? createList(source.getPage()) : new ArrayList<Row>(0);
    }

    private static List<Row> createList(SwiftNode root) {
        return root == null ? new ArrayList<Row>(0) : IteratorUtils.iterator2List(SwiftNodeUtils.node2RowIterator(root));
    }

    @Override
    public boolean hasNext() {
        if (cursor >= rows.size() && source.hasNextPage()) {
            rows = createList(source.getPage());
            cursor = 0;
        }
        return cursor < rows.size();
    }

    @Override
    public Row next() {
        final Row row = rows.get(cursor);
        cursor++;
        return row;
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void close() {
        IoUtil.close(source);
    }
}
