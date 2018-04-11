package com.fr.swift.result.node.iterator;

import com.fr.swift.result.KeyValue;
import com.fr.swift.result.node.GroupNode;
import com.fr.swift.structure.stack.ArrayLimitedStack;
import com.fr.swift.structure.stack.LimitedStack;

import java.util.Iterator;

/**
 * 深度优先的node遍历器
 *
 * Created by Lyon on 2018/4/10.
 */
public class DFTGroupNodeIterator implements Iterator<KeyValue<Integer, GroupNode>> {

    private GroupNode root;
    private LimitedStack<ChildIterator> iterators;
    private KeyValue<Integer, GroupNode> next;

    public DFTGroupNodeIterator(int nthLevel, GroupNode root) {
        this.root = root;
        this.iterators = new ArrayLimitedStack<ChildIterator>(nthLevel);
        init();
    }

    private void init() {
        iterators.push(new ChildIterator(root));
        next = new KeyValue<Integer, GroupNode>(0, root);
    }

    @Override
    public boolean hasNext() {
        return next != null;
    }

    @Override
    public KeyValue<Integer, GroupNode> next() {
        KeyValue<Integer, GroupNode> ret = next;
        while (!iterators.isEmpty()) {
            ChildIterator it = iterators.peek();
            if (it.hasNext()) {
                GroupNode node = it.next();
                next = new KeyValue<Integer, GroupNode>(iterators.size(), node);
                if (iterators.size() != iterators.limit()) {
                    iterators.push(new ChildIterator(node));
                }
                break;
            } else {
                iterators.pop();
            }
        }
        if (iterators.isEmpty()) {
            next = null;
        }
        return ret;
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }
}
