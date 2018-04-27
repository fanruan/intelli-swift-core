package com.fr.swift.query.group.by2.node;

import com.fr.swift.result.SwiftNode;
import com.fr.swift.structure.stack.ArrayLimitedStack;
import com.fr.swift.structure.stack.LimitedStack;

import java.util.List;

/**
 * 实现无查找构建树
 * <p>
 * Created by Lyon on 2018/4/26.
 */
public class ProxyNodeCreatorStack<Node extends SwiftNode> implements LimitedStack<Node> {

    private LimitedStack<Node> nodeStack;
    private Node parent;

    public ProxyNodeCreatorStack(int limit, Node root) {
        this.nodeStack = new ArrayLimitedStack<Node>(limit + 1);
        nodeStack.push(root);
        parent = root;
    }

    @Override
    public boolean isEmpty() {
        return nodeStack.size() <= 1;
    }

    @Override
    public int limit() {
        return nodeStack.limit() - 1;
    }

    @Override
    public int size() {
        return nodeStack.size() - 1;
    }

    @Override
    public void push(Node item) {
        // 无需查找比较
        parent.addChild(item);
        nodeStack.push(item);
        parent = item;
    }

    @Override
    public Node pop() {
        Node ret = nodeStack.pop();
        parent = nodeStack.peek();
        return ret;
    }

    @Override
    public Node peek() {
        return nodeStack.peek();
    }

    @Override
    public List<Node> toList() {
        return nodeStack.toList().subList(1, nodeStack.limit());
    }
}
