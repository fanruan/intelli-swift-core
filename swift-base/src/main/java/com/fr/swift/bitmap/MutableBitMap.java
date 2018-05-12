package com.fr.swift.bitmap;

/**
 * @author pony
 * @date 2017/10/9
 * 可改变自身的位图索引
 */
public interface MutableBitMap extends ImmutableBitMap {
    void or(ImmutableBitMap index);

    void and(ImmutableBitMap index);

    void andNot(ImmutableBitMap index);

    void add(int index);

    void remove(int index);
}
