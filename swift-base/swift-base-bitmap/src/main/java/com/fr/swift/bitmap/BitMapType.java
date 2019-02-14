package com.fr.swift.bitmap;

/**
 * @author anchore
 */
public enum BitMapType {
    /**
     * 位图类型
     */
    ROARING_IMMUTABLE(((byte) 0)),
    ROARING_MUTABLE((byte) 1),
    BIT_SET_IMMUTABLE((byte) 2),
    BIT_SET_MUTABLE((byte) 3),
    ALL_SHOW((byte) 4),
    ID((byte) 5),
    RANGE((byte) 6);

    private byte head;

    BitMapType(byte head) {
        this.head = head;
    }

    public byte getHead() {
        return head;
    }
}
