package com.fr.swift.result.node.iterator;

import com.fr.swift.result.GroupNode;
import com.fr.swift.result.KeyValue;
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
    private LimitedStack<Iterator<GroupNode>> iterators;
    private KeyValue<Integer, GroupNode> next;

    /**
     * 根节点为第0层
     *
     * @param nthLevel
     * @param root
     */
    public DFTGroupNodeIterator(int nthLevel, GroupNode root) {
        this.root = root;
        this.iterators = new ArrayLimitedStack<Iterator<GroupNode>>(nthLevel);
        init();
    }

    private void init() {
        if (iterators.limit() != 0) {
            // 第0层只有根节点
            iterators.push(root.getChildren().iterator());
        }
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
            Iterator<GroupNode> it = iterators.peek();
            if (it.hasNext()) {
                GroupNode node = it.next();
                next = new KeyValue<Integer, GroupNode>(iterators.size(), node);
                if (iterators.size() != iterators.limit()) {
                    iterators.push(node.getChildren().iterator());
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
