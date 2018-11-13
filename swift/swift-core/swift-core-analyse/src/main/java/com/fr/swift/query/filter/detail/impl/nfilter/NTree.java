/**
 *
 */
package com.fr.swift.query.filter.detail.impl.nfilter;

import java.util.Comparator;
import java.util.Map.Entry;
import java.util.TreeMap;

/**
 * @author Daniel
 */
public class NTree<T> {


    private Comparator<T> comparator;


    private TreeMap<T, Counter> map;

    private int N;

    private int addCount = 0;

    /**
     *
     */
    public NTree(Comparator<T> comparator, int N) {
        if (N <= 0) {
            throw new UnsupportedOperationException();
        }
        this.comparator = comparator;
        this.map = new TreeMap<T, Counter>(comparator);
        this.N = N;
    }

    public void add(T t) {
        if (addCount < N) {
            putValue(t);
        } else {
            Entry<T, Counter> entry = map.lastEntry();
            if (comparator.compare(t, entry.getKey()) < 0) {
                putValue(t);
                Counter c = entry.getValue();
                c.reduce();
                if (c.isEmpty()) {
                    map.remove(entry.getKey());
                }
            }
        }
        addCount++;
    }

    public T getLineValue() {
        if (!map.isEmpty()) {
            return map.lastKey();
        }
        return null;
    }

    private void putValue(T t) {
        Counter c = map.get(t);
        if (c == null) {
            c = new Counter();
            map.put(t, c);
        }
        c.plus();
    }


    private class Counter {
        int count = 0;

        void plus() {
            count++;
        }

        void reduce() {
            count--;
        }

        boolean isEmpty() {
            return count == 0;
        }
    }
}