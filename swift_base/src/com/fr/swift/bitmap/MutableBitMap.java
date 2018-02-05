package com.fr.swift.bitmap;

/**
 * @author pony
 * @date 2017/10/9
 * 可改变自身的位图索引
 */
public interface MutableBitMap extends ImmutableBitMap {
    void or(MutableBitMap index);

    void and(MutableBitMap index);

    void andNot(MutableBitMap index);

    void add(int index);

    void remove(int index);
}
