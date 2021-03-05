package com.fr.swift.cloud.segment.column.impl.base;

import com.fr.swift.cloud.bitmap.ImmutableBitMap;
import com.fr.swift.cloud.query.formula.Formula;
import com.fr.swift.cloud.segment.Segment;
import com.fr.swift.cloud.segment.column.BitmapIndexedColumn;
import com.fr.swift.cloud.util.Crasher;

//import ColumnTypeConstants;
//import com.fr.swift.source.etl.utils.FormulaUtils;

/**
 * Created by pony on 2018/5/12.
 */
public class FormulaIndexColumn implements BitmapIndexedColumn {
    private ImmutableBitMap nullIndex;
    private Formula formula;

    public FormulaIndexColumn(Formula formula, Segment segment) {
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
    public void release() {
        Crasher.crash("unsupported");
    }

    @Override
    public boolean isReadable() {
        return nullIndex != null;
    }
}
