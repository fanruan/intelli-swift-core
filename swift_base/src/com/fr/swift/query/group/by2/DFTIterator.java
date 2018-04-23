package com.fr.swift.query.group.by2;

import com.fr.swift.structure.stack.ArrayLimitedStack;
import com.fr.swift.structure.stack.LimitedStack;

import java.util.Iterator;

/**
 * Created by Lyon on 2018/4/23.
 */
public class DFTIterator<T> implements Iterator<T>, PopUpCallback {

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
