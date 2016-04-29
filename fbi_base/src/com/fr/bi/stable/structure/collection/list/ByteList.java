package com.fr.bi.stable.structure.collection.list;

import java.io.Serializable;


/**
 * 整数链表，这个链表的所有元素都是整数
 */
public class ByteList extends BIArrayList<Byte> implements Serializable {
    @Override
    protected Byte[] generateEmptyArray(Integer capacity) {
        return new Byte[capacity];
    }
}