package com.fr.swift.result.node.iterator;

import com.fr.swift.result.GroupNode;
import com.fr.swift.structure.stack.ArrayLimitedStack;
import com.fr.swift.structure.stack.LimitedStack;

import java.util.Iterator;
import java.util.List;

/**
 * 深度优先的node遍历器
 *
 * Created by Lyon on 2018/4/10.
 */
public class DFTGroupNodeIterator implements Iterator<GroupNode> {

    private GroupNode root;
    private int[] cursor;
    private LimitedStack<Iterator<GroupNode>> iterators;
    private GroupNode next;

    /**
     * @param dimensionSize <= 0的情况下返回根节点
     * @param root
     */
    public DFTGroupNodeIterator(int dimensionSize, GroupNode root) {
        this(dimensionSize, root, new int[dimensionSize]);
    }

    public DFTGroupNodeIterator(int dimensionSize, GroupNode root, int[] cursor) {
        this.root = root;
        this.cursor = cursor;
        this.iterators = dimensionSize <= 0 ? null : new ArrayLimitedStack<Iterator<GroupNode>>(dimensionSize);
        init();
    }

    private void init() {
        if (iterators != null) {
            iterators.push(getIt(0, root.getChildren()));
        }
        next = root;
    }

    private Iterator<GroupNode> getIt(int dimensionIndex, List<GroupNode> children) {
        int index = getStartIndex(dimensionIndex);
        return index == 0 ? children.iterator() : children.subList(index, children.size()).iterator();
    }

    private int getStartIndex(int dimensionIndex) {
        int index = cursor[dimensionIndex];
        if (index != 0) {
            // 分组定位的游标只能用一次
            cursor[dimensionIndex] = 0;
        }
        return index;
    }

    @Override
    public boolean hasNext() {
        return next != null;
    }

    private GroupNode getNext() {
        GroupNode ret = null;
        while (!iterators.isEmpty()) {
            Iterator<GroupNode> it = iterators.peek();
            if (it.hasNext()) {
                GroupNode node = it.next();
                ret = node;
                if (iterators.size() != iterators.limit()) {
                    iterators.push(getIt(iterators.size(), node.getChildren()));
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
