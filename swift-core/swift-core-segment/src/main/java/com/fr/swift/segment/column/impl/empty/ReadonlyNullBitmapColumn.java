package com.fr.swift.segment.column.impl.empty;

import com.fr.swift.bitmap.ImmutableBitMap;
import com.fr.swift.bitmap.impl.RangeBitmap;
import com.fr.swift.segment.column.impl.base.BaseBitmapColumn;

/**
 * @author anchore
 * @date 2019/3/26
 */
class ReadonlyNullBitmapColumn extends BaseBitmapColumn {
    private int rowCount;

    ReadonlyNullBitmapColumn(int rowCount) {
        this.rowCount = rowCount;
    }

    @Override
    public boolean isReadable() {
        return true;
    }

    @Override
    public void putBitMapIndex(int index, ImmutableBitMap bitmap) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ImmutableBitMap getBitMapIndex(int index) {
        ReadonlyNullColumn.checkIndex(index, 1);
        return new RangeBitmap(0, rowCount);
    }

    @Override
    public void release() {
    }
}