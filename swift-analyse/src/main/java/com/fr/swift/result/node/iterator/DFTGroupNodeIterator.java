package com.fr.swift.result.node.iterator;

import com.fr.swift.result.GroupNode;
import com.fr.swift.structure.stack.ArrayLimitedStack;
import com.fr.swift.structure.stack.LimitedStack;
import com.fr.swift.util.function.Function2;

import java.util.Iterator;
import java.util.List;

/**
 * 深度优先的node遍历器
 *
 * Created by Lyon on 2018/4/10.
 */
public class DFTGroupNodeIterator implements Iterator<GroupNode> {

    public static final int DEFAULT_START_INDEX = -1;

    private GroupNode root;
    private int[] cursor;
    private LimitedStack<Iterator<GroupNode>> iterators;
    private Function2<Integer, List<GroupNode>, Iterator<GroupNode>> itGetter;
    private GroupNode next;

    /**
     * @param dimensionSize <= 0的情况下返回根节点
     * @param root
     */
    public DFTGroupNodeIterator(int dimensionSize, GroupNode root) {
        this(true, dimensionSize, root, new int[dimensionSize]);
    }

    /**
     * 可以根据游标进行向前或者向后遍历的迭代器
     *
     * @param isNormalIterator true为前序
     * @param dimensionSize
     * @param root
     * @param cursor           GroupNode#getIndex()对应的游标
     */
    public DFTGroupNodeIterator(boolean isNormalIterator, int dimensionSize, GroupNode root, int[] cursor) {
        this.root = root;
        this.cursor = cursor;
        this.iterators = dimensionSize <= 0 ? null : new ArrayLimitedStack<Iterator<GroupNode>>(dimensionSize);
        this.itGetter = isNormalIterator ? normalItGetter : reverseItGetter;
        init();
    }

    private void init() {
        if (iterators != null) {
            iterators.push(itGetter.apply(0, root.getChildren()));
        }
        next = root;
    }

    private Function2<Integer, List<GroupNode>, Iterator<GroupNode>> normalItGetter = new Function2<Integer, List<GroupNode>, Iterator<GroupNode>>() {
        @Override
        public Iterator<GroupNode> apply(Integer dimensionIndex, List<GroupNode> children) {
            int index = getStartIndex(dimensionIndex);
            return index == DEFAULT_START_INDEX || index == 0 ? children.iterator() : children.subList(index, children.size()).iterator();
        }
    };

    private Function2<Integer, List<GroupNode>, Iterator<GroupNode>> reverseItGetter = new Function2<Integer, List<GroupNode>, Iterator<GroupNode>>() {
        @Override
        public Iterator<GroupNode> apply(Integer dimensionIndex, List<GroupNode> children) {
            int index = getStartIndex(dimensionIndex);
            if (index == DEFAULT_START_INDEX) {
                return new ReverseIt(children.size() - 1, children);
            } else {
                return new ReverseIt(index, children.subList(0, index));
            }
        }
    };

    private int getStartIndex(int dimensionIndex) {
        int index = cursor[dimensionIndex];
        if (index != DEFAULT_START_INDEX) {
            // 分组定位的游标只能用一次
            cursor[dimensionIndex] = DEFAULT_START_INDEX;
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
                    iterators.push(itGetter.apply(iterators.size(), node.getChildren()));
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

    private static class ReverseIt implements Iterator<GroupNode> {

        private int index;
        private List<GroupNode> children;

        public ReverseIt(int index, List<GroupNode> children) {
            this.index = index;
            this.children = children;
        }

        @Override
        public boolean hasNext() {
            return index > -1;
        }

        @Override
        public GroupNode next() {
            return children.get(index--);
        }

        @Override
        public void remove() {
        }
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }
}
