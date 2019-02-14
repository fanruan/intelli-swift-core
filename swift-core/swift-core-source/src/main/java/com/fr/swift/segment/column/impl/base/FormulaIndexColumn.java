package com.fr.swift.segment.column.impl.base;

import com.fr.swift.bitmap.ImmutableBitMap;
import com.fr.swift.bitmap.impl.AllShowBitMap;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.column.BitmapIndexedColumn;
//import com.fr.swift.source.ColumnTypeConstants;
//import com.fr.swift.source.etl.utils.FormulaUtils;
import com.fr.swift.util.Crasher;

/**
 * Created by pony on 2018/5/12.
 */
public class FormulaIndexColumn implements BitmapIndexedColumn {
    private ImmutableBitMap nullIndex;

    public FormulaIndexColumn(String formula, Segment segment) {
//        ColumnTypeConstants.ColumnType type = FormulaUtils.getColumnType(segment.getMetaData(), formula);
//        if (type != ColumnTypeConstants.ColumnType.NUMBER) {
//            nullIndex = AllShowBitMap.of(segment.getRowCount());
//        }
    }

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
        return nullIndex;
    }

    @Override
    public void flush() {
        Crasher.crash("unsupported");
    }

    @Override
    public void release() {
        Crasher.crash("unsupported");
    }

    @Override
    public boolean isReadable() {
        return nullIndex != null;
    }
}
