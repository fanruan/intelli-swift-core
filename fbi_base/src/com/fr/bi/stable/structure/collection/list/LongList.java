package com.fr.bi.stable.structure.collection.list;

import java.io.Serializable;


/**
 * 整数链表，这个链表的所有元素都是整数
 */
public class LongList extends BIArrayList<Long> implements Serializable {
    public LongList(int initialCapacity) {
        super(initialCapacity);
    }

    public LongList() {
    }

    @Override
    protected Long[] generateEmptyArray(Integer capacity) {
        return new Long[capacity];
    }
}