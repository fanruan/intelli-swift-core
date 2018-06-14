package com.fr.swift.result;

import com.fr.swift.source.Row;
import com.fr.swift.structure.iterator.IteratorUtils;
import com.fr.swift.structure.iterator.MapperIterator;
import com.fr.swift.structure.iterator.Tree2RowIterator;
import com.fr.swift.util.function.Function;

import java.util.Iterator;
import java.util.List;

/**
 * Created by Lyon on 2018/6/13.
 */
public class SwiftRowIteratorImpl implements SwiftRowIterator {

    private NodeResultSet source;
    private List<Row> rows;
    private List<Row> nextRowList;
    private int cursor = 0;
    private volatile boolean isUpdating = false;
    private final Object lock = new Object();

    public SwiftRowIteratorImpl(NodeResultSet source) {
        this.source = source;
        this.rows = createList(source.getNode());
    }

    private static List<Row> createList(SwiftNode root) {
        return IteratorUtils.iterator2List(new MapperIterator<List<SwiftNode>, Row>(new Tree2RowIterator<SwiftNode>(SwiftNodeUtils.getDimensionSize(root), root.getChildren().iterator(), new Function<SwiftNode, Iterator<SwiftNode>>() {
            @Override
            public Iterator<SwiftNode> apply(SwiftNode p) {
                return p.getChildren().iterator();
            }
        }), new Function<List<SwiftNode>, Row>() {
            @Override
            public Row apply(List<SwiftNode> p) {
                return SwiftNodeUtils.nodes2Row(p);
            }
        }));
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
