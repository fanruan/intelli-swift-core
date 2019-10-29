package com.fr.swift.result.node.iterator;

import com.fr.swift.result.SwiftNode;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * 广度优先的node迭代器
 * Created by Lyon on 2018/4/4.
 */
public class BFTGroupNodeIterator implements Iterator<SwiftNode> {

    private LinkedList<SwiftNode> queue = new LinkedList<SwiftNode>();

    public BFTGroupNodeIterator(SwiftNode root) {
        queue.add(root);
    }

    @Override
    public boolean hasNext() {
        return !queue.isEmpty();
    }

    @Override
    public SwiftNode next() {
        SwiftNode node4Return = queue.remove();
        List<SwiftNode> children = node4Return.getChildren();
        queue.addAll(children);
        return node4Return;
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }
}
