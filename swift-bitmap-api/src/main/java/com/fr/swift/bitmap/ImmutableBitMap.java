package com.fr.swift.bitmap;


import com.fr.swift.structure.IntIterable;
import com.fr.swift.structure.iterator.RowTraversal;

/**
 * @author pony
 * @date 2017/10/9
 * 不可改变的位图
 */
public interface ImmutableBitMap extends RowTraversal, BytesGetter, IntIterable {

    /**
     * 位与
     *
     * @param index bitmap
     * @return 结果
     */
    ImmutableBitMap getAnd(ImmutableBitMap index);

    /**
     * 位或
     * @param index bitmap
     * @return 结果
     */
    ImmutableBitMap getOr(ImmutableBitMap index);

    /**
     * 取最大位较大的为边界
     * index先位反，然后与this位与
     * @param index bitmap
     * @return 结果
     */
    ImmutableBitMap getAndNot(ImmutableBitMap index);

    /**
     * 0~bound内全部取位反
     * @param bound 边界
     * @return 结果
     */
    ImmutableBitMap getNot(int bound);

    /**
     * 是否包含index
     *
     * @param index 索引
     * @return 是否包含index
     */
    boolean contains(int index);

    /**
     * 克隆自身
     *
     * @return 克隆对象
     */
    ImmutableBitMap clone();

    BitMapType getType();
}