package com.fr.swift.result.node.iterator;

import com.fr.swift.result.GroupNode;
import com.fr.swift.structure.iterator.Filter;
import com.fr.swift.structure.iterator.FilteredIterator;

import java.util.Iterator;

/**
 * 用于“所有值”类的计算指标计算。比如根据所有值累计、求和、平均等。
 *
 * Created by Lyon on 2018/4/4.
 */
public class LeafNodeIterator implements Iterator<GroupNode> {

    private Iterator<GroupNode> iterator;

    public LeafNodeIterator(GroupNode root) {
        Iterator<GroupNode> iterator = new BFTGroupNodeIterator(root);
        this.iterator = new FilteredIterator<GroupNode>(iterator, new Filter<GroupNode>() {
            @Override
            public boolean accept(GroupNode biGroupNode) {
                return biGroupNode.getChildrenSize() == 0;
            }
        });
    }

    @Override
    public boolean hasNext() {
        return iterator.hasNext();
    }

    @Override
    public GroupNode next() {
        return iterator.next();
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }
}
