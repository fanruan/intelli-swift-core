package com.fr.swift.query.group.by2;

import com.fr.swift.query.group.by.GroupByEntry;
import com.fr.swift.structure.stack.ArrayLimitedStack;
import com.fr.swift.structure.stack.LimitedStack;

import java.util.Iterator;

/**
 * 尝试对多维GroupBy的逻辑做进一步拆分
 *
 * Created by Lyon on 2018/4/20.
 */
public class MultiGroupByV2 implements Iterator<GroupByEntry[]> {

    private DFTIterator<GroupByEntry> iterator;
    private LimitedStack<GroupByEntry> entries;
    private GroupByEntry[] next;
    private GroupByController controller;

    public MultiGroupByV2(int dimensionSize, DFTIterator<GroupByEntry> iterator, GroupByController controller) {
        this.iterator = iterator;
        this.controller = controller;
        entries = new ArrayLimitedStack<GroupByEntry>(dimensionSize);
        next = getNext();
    }

    private GroupByEntry[] getNext() {
        GroupByEntry[] ret = null;
        while (iterator.hasNext()) {
            entries.push(iterator.next());
            ret = entries.toArray();
            // 判断是否为满足要求的一行
            if (controller.isRow(ret, iterator)) {
                entries.pop();
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
    public GroupByEntry[] next() {
        GroupByEntry[] entries = next;
        next = getNext();
        return entries;
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }
}
