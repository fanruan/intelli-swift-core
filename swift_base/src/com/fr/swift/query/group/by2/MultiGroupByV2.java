package com.fr.swift.query.group.by2;

import com.fr.swift.query.group.by.GroupBy;
import com.fr.swift.query.group.by.GroupByEntry;
import com.fr.swift.segment.column.Column;
import com.fr.swift.structure.iterator.RowTraversal;
import com.fr.swift.structure.stack.ArrayLimitedStack;
import com.fr.swift.structure.stack.LimitedStack;

import java.util.Iterator;
import java.util.List;

/**
 * Created by Lyon on 2018/4/20.
 */
public class MultiGroupByV2 implements Iterator<List<GroupByEntry>> {

    private List<Column> dimensions;
    private RowTraversal filter;
    private Iterator<GroupByEntry> iterator;
    private LimitedStack<GroupByEntry> entries;
    private List<GroupByEntry> next;


    public MultiGroupByV2(List<Column> dimensions, RowTraversal filter) {
        this.dimensions = dimensions;
        this.filter = filter;
        this.entries = new ArrayLimitedStack<GroupByEntry>(dimensions.size());
    }

    private void init() {
        boolean[] asc = new boolean[dimensions.size()];
        int[] cursor = new int[dimensions.size()];
        iterator = new DFTIterator<GroupByEntry>(dimensions.size(), new Creator(asc, cursor, dimensions, filter));
    }

    private List<GroupByEntry> getNext() {
        List<GroupByEntry> ret = null;
        return null;
    }

    @Override
    public boolean hasNext() {
        return false;
    }

    @Override
    public List<GroupByEntry> next() {
        return null;
    }

    @Override
    public void remove() {

    }
}

class DFTIterator<T> implements Iterator<T>, PopUpCallBack {

    private LimitedStack<Iterator<T>> iterators;
    private IteratorCreator<T> creator;
    private T next;


    public DFTIterator(int limit, IteratorCreator<T> creator) {
        this.creator = creator;
        this.iterators = new ArrayLimitedStack<Iterator<T>>(limit);
        init();
    }

    private void init() {
        Iterator<T> iterator = creator.createIterator(0, null);
        if (!iterators.isEmpty()) {
            iterators.push(iterator);
        }
        next = getNext();
    }

    private T getNext() {
        T ret = null;
        while (!iterators.isEmpty()) {
            Iterator<T> it = iterators.peek();
            if (it.hasNext()) {
                ret = it.next();
                if (iterators.size() != iterators.limit()) {
                    iterators.push(creator.createIterator(iterators.size(), ret));
                }
                break;
            } else {
                iterators.pop();
            }
        }
        return ret;
    }

    @Override
    public boolean hasNext() {
        return next != null;
    }

    @Override
    public T next() {
        T ret = next;
        next = getNext();
        return ret;
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void pop() {
        iterators.pop();
    }
}

class Creator implements IteratorCreator<GroupByEntry> {

    private boolean[] asc;
    private int[] cursor;
    private List<Column> dimensions;
    private RowTraversal filter;

    public Creator(boolean[] asc, int[] cursor, List<Column> dimensions, RowTraversal filter) {
        this.asc = asc;
        this.cursor = cursor;
        this.dimensions = dimensions;
        this.filter = filter;
    }

    @Override
    public Iterator<GroupByEntry> createIterator(int stackSize, GroupByEntry groupByEntry) {
        if (groupByEntry == null && stackSize == 0) {
            return GroupBy.createGroupByResult(dimensions.get(0), filter, cursor[0], asc[0]);
        }
        return GroupBy.createGroupByResult(dimensions.get(stackSize), groupByEntry.getTraversal(),
                cursor[stackSize], asc[stackSize]);
    }
}

interface IteratorCreator<ENTRY> {

    Iterator<ENTRY> createIterator(int stackSize, ENTRY entry);
}

interface PopUpCallBack {

    void pop();
}
