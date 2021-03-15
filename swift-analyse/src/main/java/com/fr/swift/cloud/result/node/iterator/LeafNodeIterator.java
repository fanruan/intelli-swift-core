package com.fr.swift.cloud.result.node.iterator;

import com.fr.swift.cloud.result.SwiftNode;
import com.fr.swift.cloud.structure.iterator.Filter;
import com.fr.swift.cloud.structure.iterator.FilteredIterator;

import java.util.Iterator;

/**
 * 用于“所有值”类的计算指标计算。比如根据所有值累计、求和、平均等。
 *
 * Created by Lyon on 2018/4/4.
 */
public class LeafNodeIterator implements Iterator<SwiftNode> {

    private Iterator<SwiftNode> iterator;

    public LeafNodeIterator(SwiftNode root) {
        Iterator<SwiftNode> iterator = new BFTGroupNodeIterator(root);
        this.iterator = new FilteredIterator<SwiftNode>(iterator, new Filter<SwiftNode>() {
            @Override
            public boolean accept(SwiftNode biGroupNode) {
                return biGroupNode.getChildrenSize() == 0;
            }
        });
    }

    @Override
    public boolean hasNext() {
        return iterator.hasNext();
    }

    @Override
    public SwiftNode next() {
        return iterator.next();
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }
}
