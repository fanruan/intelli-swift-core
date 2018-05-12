package com.fr.swift.query.adapter.metric;

import com.fr.swift.bitmap.ImmutableBitMap;
import com.fr.swift.segment.column.BitmapIndexedColumn;
import com.fr.swift.util.Crasher;

/**
 * Created by pony on 2018/5/12.
 */
public class FormulaIndexColumn implements BitmapIndexedColumn {
    @Override
    public void putBitMapIndex(int index, ImmutableBitMap bitmap) {
        Crasher.crash("unsupported");
    }

    @Override
    public ImmutableBitMap getBitMapIndex(int index) {
        return Crasher.crash("unsupported");
    }

    @Override
    public void putNullIndex(ImmutableBitMap bitMap) {
        Crasher.crash("unsupported");
    }

    @Override
    public ImmutableBitMap getNullIndex() {
        return null;
    }

    @Override
    public void flush() {
        Crasher.crash("unsupported");
    }

    @Override
    public void release() {
        Crasher.crash("unsupported");
    }
}
