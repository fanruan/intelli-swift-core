package com.fr.swift.structure;

import java.util.Iterator;

/**
 * @author anchore
 * @date 2018/12/12
 */
public interface IntIterable extends Iterable<Integer> {

    IntIterator intIterator();

    interface IntIterator extends Iterator<Integer> {

        int nextInt();
    }
}