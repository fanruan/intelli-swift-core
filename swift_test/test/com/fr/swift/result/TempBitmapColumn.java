package com.fr.swift.result;

import com.fr.swift.bitmap.ImmutableBitMap;
import com.fr.swift.segment.column.BitmapIndexedColumn;

public class TempBitmapColumn implements BitmapIndexedColumn {
    @Override
    public void putBitMapIndex(int index, ImmutableBitMap bitmap) {

    }

    @Override
    public ImmutableBitMap getBitMapIndex(int index) {
        return null;
    }

    @Override
    public void putNullIndex(ImmutableBitMap bitMap) {

    }

    @Override
    public ImmutableBitMap getNullIndex() {
        return null;
    }

    @Override
    public void flush() {

    }

    @Override
    public void release() {

    }
}
