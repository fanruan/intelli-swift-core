package com.fr.bi.stable.structure.collection.list;

import java.io.Serializable;


/**
 * 整数链表，这个链表的所有元素都是整数
 */
public class DoubleList extends BIArrayList<Double> implements Serializable {
    @Override
    protected Double[] generateEmptyArray(Integer capacity) {
        return new Double[capacity];
    }
}