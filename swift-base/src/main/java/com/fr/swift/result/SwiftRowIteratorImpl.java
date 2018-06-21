package com.fr.swift.result;

import com.fr.swift.source.Row;

import java.util.List;

/**
 * todo 和SwiftNode2RowIterator高度相似，待重构
 *
 * Created by Lyon on 2018/6/19.
 */
public class SwiftRowIteratorImpl implements SwiftRowIterator {

    private DetailResultSet source;
    private List<Row> rows;
    private List<Row> nextRowList;
    private int cursor = 0;
    private volatile boolean isUpdating = false;
    private final Object lock = new Object();

    public SwiftRowIteratorImpl(DetailResultSet source) {
        this.source = source;
        this.rows = source.getPage();
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
                    nextRowList = source.getPage();
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
