package com.fr.swift.query.group.by2.node.iterator;

import com.fr.swift.query.group.by.GroupByEntry;
import com.fr.swift.query.group.info.GroupByInfo;
import com.fr.swift.result.XLeftNode;
import com.fr.swift.structure.stack.LimitedStack;
import com.fr.swift.util.function.BiFunction;

import java.util.Iterator;

/**
 * Created by Lyon on 2018/4/27.
 */
public class XLeftNodeIterator implements Iterator<XLeftNode[]> {

    private GroupByInfo rowGroupByInfo;
    private XLeftNode root;
    private BiFunction<GroupByEntry, LimitedStack<XLeftNode>, XLeftNode[]> rowMapper;
    private Iterator<XLeftNode[]> iterator;

    public XLeftNodeIterator(GroupByInfo rowGroupByInfo, XLeftNode root,
                             BiFunction<GroupByEntry, LimitedStack<XLeftNode>, XLeftNode[]> rowMapper) {
        this.rowGroupByInfo = rowGroupByInfo;
        this.root = root;
        this.rowMapper = rowMapper;
        init();
    }

    private void init() {
        BiFunction<Integer, GroupByEntry, XLeftNode> itemMapper = new BiFunction<Integer, GroupByEntry, XLeftNode>() {
            @Override
            public XLeftNode apply(Integer deep, GroupByEntry groupByEntry) {
                return new XLeftNode((int) deep, groupByEntry.getIndex());
            }
        };
        iterator = new GroupNodeIterator<XLeftNode>(rowGroupByInfo, root, itemMapper, rowMapper);
    }

    @Override
    public boolean hasNext() {
        return iterator.hasNext();
    }

    @Override
    public XLeftNode[] next() {
        return iterator.next();
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }
}
