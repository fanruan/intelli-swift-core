package com.fr.swift.result.node.iterator;

import com.fr.swift.result.GroupNode;
import com.fr.swift.result.node.FIFOQueue;
import com.fr.swift.result.node.LinkedListFIFOQueue;

import java.util.Iterator;
import java.util.List;

/**
 * 广度优先的node迭代器
 * Created by Lyon on 2018/4/4.
 */
public class BFTGroupNodeIterator implements Iterator<GroupNode> {

    FIFOQueue<GroupNode> queue = new LinkedListFIFOQueue<GroupNode>();

    public BFTGroupNodeIterator(GroupNode root) {
        queue.add(root);
    }

    @Override
    public boolean hasNext() {
        return !queue.isEmpty();
    }

    @Override
    public GroupNode next() {
        GroupNode node4Return = queue.remove();
        List<GroupNode> children = node4Return.getChildren();
        for (GroupNode child : children) {
            queue.add(child);
        }
        return node4Return;
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }
}
