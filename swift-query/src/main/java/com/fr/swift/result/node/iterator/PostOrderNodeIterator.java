package com.fr.swift.result.node.iterator;

import com.fr.swift.result.SwiftNode;
import com.fr.swift.structure.stack.ArrayLimitedStack;
import com.fr.swift.structure.stack.LimitedStack;

import java.util.Iterator;

/**
 * 后续(LRD)遍历node迭代器
 *
 * Created by Lyon on 2018/4/10.
 */
class PostOrderNodeIterator implements Iterator<SwiftNode> {

    private LimitedStack<SwiftNode> parentNodes;
    private LimitedStack<Iterator<SwiftNode>> iterators;
    private SwiftNode root;
    private SwiftNode next = null;

    public PostOrderNodeIterator(int dimensionSize, SwiftNode root) {
        parentNodes = new ArrayLimitedStack<SwiftNode>(dimensionSize);
        iterators = new ArrayLimitedStack<Iterator<SwiftNode>>(dimensionSize);
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

    private SwiftNode getNext() {
        SwiftNode n = null;
        while (!iterators.isEmpty()) {
            Iterator<SwiftNode> it = iterators.peek();
            if (it.hasNext()) {
                SwiftNode node = it.next();
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
    public SwiftNode next() {
        SwiftNode ret = next;
        next = getNext();
        return ret;
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }
}
