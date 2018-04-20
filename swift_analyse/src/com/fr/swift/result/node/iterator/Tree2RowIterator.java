package com.fr.swift.result.node.iterator;

import com.fr.swift.structure.stack.ArrayLimitedStack;
import com.fr.swift.structure.stack.LimitedStack;
import com.fr.swift.util.function.Supplier;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * 树转为行，深度优先遍历
 *
 * Created by Lyon on 2018/4/19.
 */
public class Tree2RowIterator<E, TREE extends Supplier<E> & Iterable<TREE>> implements Iterator<List<E>> {

    private LimitedStack<Iterator<TREE>> iterators;
    private LimitedStack<E> elements;
    private TREE root;
    private E[] next;

    public Tree2RowIterator(int limitLevel, TREE root) {
        this.root = root;
        this.iterators = new ArrayLimitedStack<Iterator<TREE>>(limitLevel - 1);
        this.elements = new ArrayLimitedStack<E>(limitLevel);
        init();
    }

    private void init() {
        this.iterators.push(root.iterator());
        this.elements.push(root.get());
        next = getNext();
    }

    private E[] getNext() {
        E[] ret = null;
        while (!iterators.isEmpty()) {
            Iterator<TREE> it = iterators.peek();
            if (it.hasNext()) {
                TREE node = it.next();
                elements.push(node.get());
                if (iterators.size() != iterators.limit() && node.iterator().hasNext()) {
                    iterators.push(node.iterator());
                    // 因为要按行返回，索引这里要继续循环，直到遇到叶子节点（普通叶子节点或者是第limitLevel层的节点）
                    continue;
                }
            } else {
                iterators.pop();
                elements.pop();
                continue;
            }
            // 执行到这里说明找到了满足要求的叶子节点
            ret = elements.toArray();
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
    public List<E> next() {
        E[] ret = next;
        next = getNext();
        // 这边要返回
        return Arrays.asList(ret);
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }
}
