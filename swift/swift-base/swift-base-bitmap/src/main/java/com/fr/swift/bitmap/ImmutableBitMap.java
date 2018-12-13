package com.fr.swift.bitmap;


import com.fr.swift.structure.IntIterable;
import com.fr.swift.structure.iterator.RowTraversal;

/**
 * @author pony
 * @date 2017/10/9
 * 不可改变的位图
 */
public interface ImmutableBitMap extends RowTraversal, BytesGetter, IntIterable {

    ImmutableBitMap getAnd(ImmutableBitMap index);

    ImmutableBitMap getOr(ImmutableBitMap index);

    ImmutableBitMap getAndNot(ImmutableBitMap index);

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
