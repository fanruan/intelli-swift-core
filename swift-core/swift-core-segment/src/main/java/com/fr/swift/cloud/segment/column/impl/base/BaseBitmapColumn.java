package com.fr.swift.cloud.segment.column.impl.base;

import com.fr.swift.cloud.bitmap.ImmutableBitMap;
import com.fr.swift.cloud.segment.column.BitmapIndexedColumn;

/**
 * @author anchore
 * @date 2018/8/16
 */
public abstract class BaseBitmapColumn implements BitmapIndexedColumn {
    @Override
    public void putNullIndex(ImmutableBitMap bitMap) {
        putBitMapIndex(0, bitMap);
    }

    @Override
    public ImmutableBitMap getNullIndex() {
        return getBitMapIndex(0);
    }
}