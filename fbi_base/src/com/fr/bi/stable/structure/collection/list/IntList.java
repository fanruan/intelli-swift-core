package com.fr.bi.stable.structure.collection.list;

import java.io.Serializable;


/**
 * 整数链表，这个链表的所有元素都是整数
 */
public class IntList  extends BIArrayList<Integer> implements Serializable {

    public IntList(int initialCapacity) {
        super(initialCapacity);
    }

    public IntList() {
    }

    @Override
    protected Integer[] generateEmptyArray(Integer capacity) {
        return new Integer[capacity];
    }
}