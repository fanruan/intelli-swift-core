package com.fr.swift.structure.iterator;

import com.fr.swift.structure.stack.ArrayLimitedStack;
import com.fr.swift.structure.stack.LimitedStack;
import com.fr.swift.util.function.Function;

import java.util.Iterator;
import java.util.List;

/**
 * 树转为行，深度优先遍历
 * <p>
 * Created by Lyon on 2018/4/19.
 */
public class Tree2RowIterator<TREE> implements Iterator<List<TREE>> {

    private LimitedStack<Iterator<TREE>> iterators;
    private LimitedStack<TREE> elements;
    private Iterator<TREE> rootIt;
    private Function<TREE, Iterator<TREE>> itCreator;
    private List<TREE> next;

    public Tree2RowIterator(int limitLevel, Iterator<TREE> rootIt, Function<TREE, Iterator<TREE>> itCreator) {
        this.rootIt = rootIt;
        this.iterators = new ArrayLimitedStack<Iterator<TREE>>(limitLevel);
        this.elements = new ArrayLimitedStack<TREE>(limitLevel);
        this.itCreator = itCreator;
        init(limitLevel);
    }

    private void init(int limitLevel) {
        if (limitLevel <= 0) {
            return;
        }
        this.iterators.push(rootIt);
        next = getNext();
    }

    private List<TREE> getNext() {
        List<TREE> ret = null;
        while (!iterators.isEmpty()) {
            Iterator<TREE> it = iterators.peek();
            if (it.hasNext()) {
                TREE node = it.next();
                elements.push(node);
                Iterator<TREE> childIterator = itCreator.apply(node);
                if (iterators.size() != iterators.limit() && childIterator.hasNext()) {
                    iterators.push(childIterator);
                    // 因为要按行返回，所以这里要继续循环，直到遇到叶子节点（普通叶子节点或者是第limitLevel层的节点）
                    continue;
                }
            } else {
                iterators.pop();
                if (!elements.isEmpty()) {
                    elements.pop();
                }
                continue;
            }
            // 执行到这里说明找到了满足要求的叶子节点
            ret = elements.toList();
            elements.pop();
            break;
        }
        return ret;
    }

    @Override
    public boolean hasNext() {
        return next != null;
    }

    @Override
    public List<TREE> next() {
        List<TREE> ret = next;
        next = getNext();
        return ret;
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }
}
