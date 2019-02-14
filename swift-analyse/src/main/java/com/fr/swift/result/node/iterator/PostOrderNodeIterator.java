package com.fr.swift.result.node.iterator;

import com.fr.swift.result.GroupNode;
import com.fr.swift.structure.stack.ArrayLimitedStack;
import com.fr.swift.structure.stack.LimitedStack;

import java.util.Iterator;

/**
 * 后续(LRD)遍历node迭代器
 *
 * Created by Lyon on 2018/4/10.
 */
public class PostOrderNodeIterator<N extends GroupNode> implements Iterator<N> {

    private LimitedStack<N> parentNodes;
    private LimitedStack<Iterator<N>> iterators;
    private N root;
    private N next = null;

    public PostOrderNodeIterator(int dimensionSize, N root) {
        parentNodes = new ArrayLimitedStack<N>(dimensionSize);
        iterators = new ArrayLimitedStack<Iterator<N>>(dimensionSize);
        this.root = root;
        init();
    }

    private void init() {
        if (parentNodes.limit() == 0) {
            // 说明传过来的dimensionSize为0，这种情况向返回root
            next = root;
            return;
        }
        parentNodes.push(root);
        iterators.push(root.getChildren().iterator());
        next = getNext();
    }

    private N getNext() {
        N n = null;
        while (!iterators.isEmpty()) {
            Iterator<N> it = iterators.peek();
            if (it.hasNext()) {
                N node = it.next();
                if (iterators.size() != iterators.limit()) {
                    iterators.push(node.getChildren().iterator());
                    parentNodes.push(node);
                } else {
                    // 此时node为叶子节点
                    n = node;
                    break;
                }
            } else {
                iterators.pop();
                // 此时说明父节点的子节点迭代完了，返回父节点
                n = parentNodes.pop();
                break;
            }
        }
        return n;
    }

    @Override
    public boolean hasNext() {
        return next != null;
    }

    @Override
    public N next() {
        N ret = next;
        next = getNext();
        return ret;
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }
}
