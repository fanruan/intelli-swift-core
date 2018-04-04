package com.fr.swift.result.node.iterator;

import com.fr.swift.result.node.FIFOQueue;
import com.fr.swift.result.node.GroupNode;
import com.fr.swift.result.node.LinkedListFIFOQueue;

import java.util.Iterator;

/**
 * 广度优先的node迭代器
 * Created by Lyon on 2018/4/4.
 */
public class GroupNodeIterator implements Iterator<GroupNode> {

    FIFOQueue<GroupNode> queue = new LinkedListFIFOQueue<GroupNode>();

    public GroupNodeIterator(GroupNode root) {
        queue.add(root);
    }

    @Override
    public boolean hasNext() {
        return !queue.isEmpty();
    }

    @Override
    public GroupNode next() {
        GroupNode node4Return = queue.remove();
        GroupNode tmp = node4Return;
        while (tmp.getSibling() != null) {
            tmp = tmp.getSibling();
            queue.add(tmp.getSibling());
        }
        return node4Return;
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }
}
