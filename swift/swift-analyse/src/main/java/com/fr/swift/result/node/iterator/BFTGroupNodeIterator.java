package com.fr.swift.result.node.iterator;

import com.fr.swift.result.GroupNode;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * 广度优先的node迭代器
 * Created by Lyon on 2018/4/4.
 */
public class BFTGroupNodeIterator implements Iterator<GroupNode> {

    private LinkedList<GroupNode> queue = new LinkedList<GroupNode>();

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
