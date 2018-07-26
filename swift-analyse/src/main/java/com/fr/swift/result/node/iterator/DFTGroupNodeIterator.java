package com.fr.swift.result.node.iterator;

import com.fr.swift.result.GroupNode;
import com.fr.swift.structure.stack.ArrayLimitedStack;
import com.fr.swift.structure.stack.LimitedStack;

import java.util.Iterator;

/**
 * 深度优先的node遍历器
 *
 * Created by Lyon on 2018/4/10.
 */
public class DFTGroupNodeIterator implements Iterator<GroupNode> {

    private GroupNode root;
    private LimitedStack<Iterator<GroupNode>> iterators;
    private GroupNode next;

    /**
     * @param dimensionSize <= 0的情况下返回根节点
     * @param root
     */
    public DFTGroupNodeIterator(int dimensionSize, GroupNode root) {
        this.root = root;
        this.iterators = dimensionSize <= 0 ? null : new ArrayLimitedStack<Iterator<GroupNode>>(dimensionSize);
        init();
    }

    private void init() {
        if (iterators != null) {
            iterators.push(root.getChildren().iterator());
        }
        next = root;
    }

    @Override
    public boolean hasNext() {
        return next != null;
    }

    private GroupNode getNext() {
        GroupNode ret = null;
        while (iterators != null && !iterators.isEmpty()) {
            Iterator<GroupNode> it = iterators.peek();
            if (it.hasNext()) {
                GroupNode node = it.next();
                ret = node;
                if (iterators.size() != iterators.limit()) {
                    iterators.push(node.getChildren().iterator());
                }
                break;
            } else {
                iterators.pop();
            }
        }
        return ret;
    }

    @Override
    public GroupNode next() {
        GroupNode ret = next;
        next = getNext();
        return ret;
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }
}
