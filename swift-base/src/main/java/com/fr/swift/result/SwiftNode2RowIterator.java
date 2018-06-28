package com.fr.swift.result;

import com.fr.swift.source.Row;
import com.fr.swift.structure.iterator.IteratorUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lyon on 2018/6/13.
 */
public class SwiftNode2RowIterator implements SwiftRowIterator {

    private NodeResultSet source;
    private List<Row> rows;
    private List<Row> nextRowList;
    private int cursor = 0;
    private volatile boolean isUpdating = false;
    private final Object lock = new Object();

    public SwiftNode2RowIterator(NodeResultSet source) {
        this.source = source;
        this.rows = createList(source.getNode());
    }

    private static List<Row> createList(SwiftNode root) {
        return root == null ? new ArrayList<Row>() : IteratorUtils.iterator2List(SwiftNodeUtils.node2RowIterator(root));
    }

    /**
     * 异步更新，类似buffer的作用
     */
    private void update() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                synchronized (lock) {
                    isUpdating = true;
                    nextRowList = createList(source.getNode());
                    isUpdating = false;
                }
            }
        }).start();
    }

    @Override
    public boolean hasNext() {
        if (cursor >= rows.size()) {
            synchronized (lock) {
                if (nextRowList != null) {
                    rows = nextRowList;
                    cursor = 0;
                    nextRowList = null;
                }
            }
        }
        return cursor < rows.size();
    }

    private boolean shouldUpdate() {
        // TODO: 2018/6/13 暂定大于当前buffer的一半开始更新
        return cursor > rows.size() / 2 && !isUpdating && nextRowList == null && source.hasNextPage();
    }

    @Override
    public Row next() {
        if (shouldUpdate()) {
            // 达到某个临界值开始更新buffer
            update();
        }
        return rows.get(cursor++);
    }

    @Override
    public void remove() {
    }
}
