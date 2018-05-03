package com.fr.swift.query.group.by2.node.iterator;

import com.fr.swift.query.group.by.GroupByEntry;
import com.fr.swift.query.group.info.GroupByInfo;
import com.fr.swift.result.TopGroupNode;
import com.fr.swift.structure.stack.LimitedStack;
import com.fr.swift.util.function.Function2;

import java.util.Iterator;

/**
 * Created by Lyon on 2018/4/28.
 */
public class TopGroupNodeIterator implements Iterator<TopGroupNode[]> {

    private GroupByInfo colGroupByInfo;
    private TopGroupNode root;
    private Function2<GroupByEntry, LimitedStack<TopGroupNode>, TopGroupNode[]> rowMapper;
    private Iterator<TopGroupNode[]> iterator;

    public TopGroupNodeIterator(GroupByInfo colGroupByInfo, TopGroupNode root,
                                Function2<GroupByEntry, LimitedStack<TopGroupNode>, TopGroupNode[]> rowMapper) {
        this.colGroupByInfo = colGroupByInfo;
        this.root = root;
        this.rowMapper = rowMapper;
        init();
    }

    private void init() {
        Function2<Integer, GroupByEntry, TopGroupNode> itemMapper = new Function2<Integer, GroupByEntry, TopGroupNode>() {
            @Override
            public TopGroupNode apply(Integer deep, GroupByEntry groupByEntry) {
                return new TopGroupNode((int) deep, groupByEntry.getIndex());
            }
        };
        iterator = new GroupNodeIterator<TopGroupNode>(colGroupByInfo, root, itemMapper, rowMapper);
    }

    @Override
    public boolean hasNext() {
        return iterator.hasNext();
    }

    @Override
    public TopGroupNode[] next() {
        return iterator.next();
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }
}
