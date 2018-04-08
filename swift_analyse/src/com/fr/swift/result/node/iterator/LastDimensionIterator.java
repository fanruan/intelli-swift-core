package com.fr.swift.result.node.iterator;

import com.fr.swift.query.group.by.paging.Filter;
import com.fr.swift.query.group.by.paging.FilteredIterator;
import com.fr.swift.query.group.by.paging.MapperIterator;
import com.fr.swift.result.node.GroupNode;
import com.fr.swift.util.function.Function;

import java.util.Iterator;

/**
 * 用于“所有值”类的计算指标计算。比如根据所有值累计、求和、平均等。
 *
 * Created by Lyon on 2018/4/4.
 */
public class LastDimensionIterator implements Iterator<Number[]> {

    private Iterator<Number[]> rows;

    public LastDimensionIterator(GroupNode root) {
        init(root);
    }

    private void init(GroupNode root) {
        Iterator<GroupNode> iterator = new GroupNodeIterator(root);
        FilteredIterator<GroupNode> filteredIterator = new FilteredIterator<GroupNode>(iterator, new Filter<GroupNode>() {
            @Override
            public boolean accept(GroupNode biGroupNode) {
                return biGroupNode.getChildrenSize() == 0;
            }
        });
        rows = new MapperIterator<GroupNode, Number[]>(filteredIterator, new Function<GroupNode, Number[]>() {
            @Override
            public Number[] apply(GroupNode p) {
                return p.getSummaryValue();
            }
        });
    }

    @Override
    public boolean hasNext() {
        return rows.hasNext();
    }

    @Override
    public Number[] next() {
        return rows.next();
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }
}
