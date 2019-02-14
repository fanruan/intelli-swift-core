package com.fr.swift.bitmap;

/**
 * @author pony
 * @date 2017/10/9
 * 可改变自身的位图索引
 */
public interface MutableBitMap extends ImmutableBitMap {

    /**
     * 自身位或
     *
     * @param index bitmap
     */
    void or(ImmutableBitMap index);

    /**
     * 自身位与
     *
     * @param index bitmap
     */
    void and(ImmutableBitMap index);

    /**
     * 自身位反
     *
     * @param bound 边界
     */
    void not(int bound);

    /**
     * index先位反，然后与自身位与
     * @param index bitmap
     */
    void andNot(ImmutableBitMap index);

    /**
     * 设置第index位为1
     * @param index i
     */
    void add(int index);

    /**
     * 设置第index位为0
     * @param index i
     */
    void remove(int index);
}