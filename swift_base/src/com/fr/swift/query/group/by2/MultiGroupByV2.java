package com.fr.swift.query.group.by2;

import com.fr.swift.query.group.by.GroupByEntry;
import com.fr.swift.structure.stack.LimitedStack;
import com.fr.swift.util.function.Function2;

import java.util.Iterator;

/**
 * 尝试对多维GroupBy的逻辑做进一步拆分
 *
 * Created by Lyon on 2018/4/20.
 */
public class MultiGroupByV2<T> implements Iterator<T[]> {

    private DFTIterator iterator;
    private LimitedStack<T> itemsStack;
    private GroupByController<T> controller;
    private Function2<Integer, GroupByEntry, T> itemMapper;
    private Function2<GroupByEntry, LimitedStack<T>, T[]> rowMapper;
    private T[] next;

    /**
     * @param iterator   深度优先的树迭代器 & PopUpCallback回调接口
     * @param itemsStack groupBy过程中一行items的栈容器，这个容器可能被代理了
     * @param controller 是否为一行的控制器，通过GroupByController#isRow(T[] entries, PopUpCallback callback)控制树迭代器
     * @param itemMapper item处理函数
     * @param rowMapper  行结果处理函数
     */
    public MultiGroupByV2(DFTIterator iterator, LimitedStack<T> itemsStack, GroupByController<T> controller,
                          Function2<Integer, GroupByEntry, T> itemMapper, Function2<GroupByEntry, LimitedStack<T>, T[]> rowMapper) {
        this.iterator = iterator;
        this.controller = controller;
        this.itemsStack = itemsStack;
        this.itemMapper = itemMapper;
        this.rowMapper = rowMapper;
        next = getNext();
    }

    private T[] getNext() {
        T[] ret = null;
        while (iterator.hasNext()) {
            GroupByEntry entry = iterator.next();
            // itemsStack.size()为节点的深度，第一个维度深度为0
            itemsStack.push(itemMapper.apply(itemsStack.size(), entry));
            // 判断是否为满足要求的一行
            if (controller.isRow(itemsStack.toList(), iterator)) {
                // rowMapper对groupBy的一行进行处理，entry对应当前行的索引
                ret = rowMapper.apply(entry, itemsStack);
                itemsStack.pop();
                break;
            }
        }
        return ret;
    }

    @Override
    public boolean hasNext() {
        return next != null;
    }

    @Override
    public T[] next() {
        T[] entries = next;
        next = getNext();
        return entries;
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }
}
