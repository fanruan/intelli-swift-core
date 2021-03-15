package com.fr.swift.cloud.bitmap;

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
    RANGE((byte) 6),
    EMPTY((byte) 7);

    private byte head;

    BitMapType(byte head) {
        this.head = head;
    }

    public byte getHead() {
        return head;
    }

    public static BitMapType ofHead(byte b) {
        switch (b) {
            case 0:
                return ROARING_IMMUTABLE;
            case 1:
                return ROARING_MUTABLE;
            case 2:
                return BIT_SET_IMMUTABLE;
            case 3:
                return BIT_SET_MUTABLE;
            case 4:
                return ALL_SHOW;
            case 5:
                return ID;
            case 6:
                return RANGE;
            case 7:
                return EMPTY;
            default:
                throw new IllegalArgumentException(String.format("unknown bitmap head %d", b));
        }
    }
}
