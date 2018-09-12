package com.fr.swift.result;

import com.fr.swift.source.Row;
import com.fr.swift.structure.iterator.IteratorUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lyon on 2018/6/13.
 */
public class SwiftNode2RowIterator implements SwiftRowIterator {

    private NodeResultSet<SwiftNode> source;
    private List<Row> rows;
    private int cursor = 0;

    public SwiftNode2RowIterator(NodeResultSet<SwiftNode> source) {
        this.source = source;
        this.rows = source.hasNextPage() ? createList(source.getPage().getKey()) : new ArrayList<Row>(0);
    }

    private static List<Row> createList(SwiftNode root) {
        return root == null ? new ArrayList<Row>(0) : IteratorUtils.iterator2List(SwiftNodeUtils.node2RowIterator(root));
    }

    @Override
    public boolean hasNext() {
        if (cursor >= rows.size() && source.hasNextPage()) {
            rows = createList(source.getPage().getKey());
            cursor = 0;
        }
        return cursor < rows.size();
    }

    @Override
    public Row next() {
        return rows.get(cursor++);
    }

    @Override
    public void remove() {
    }
}
