package com.fr.swift.result.node.iterator;

import com.fr.swift.result.node.FIFOQueue;
import com.fr.swift.result.node.GroupNode;
import com.fr.swift.result.node.LinkedListFIFOQueue;

import java.util.Iterator;

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
        GroupNode tmp = node4Return.getChildrenSize() == 0 ? null : node4Return.getChild(0);
        while (tmp != null) {
            queue.add(tmp);
            tmp = tmp.getSibling();
        }
        return node4Return;
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }
}
