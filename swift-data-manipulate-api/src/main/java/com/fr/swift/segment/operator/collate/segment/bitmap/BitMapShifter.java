package com.fr.swift.segment.operator.collate.segment.bitmap;

import com.fr.swift.bitmap.ImmutableBitMap;
import com.fr.swift.structure.iterator.RowTraversal;

/**
 * Created by lyon on 2019/2/20.
 */
public interface BitMapShifter {

    /**
     * 计算出偏移后的bitmap
     *
     * @param original
     * @return
     */
    ImmutableBitMap shift(RowTraversal original);
}
