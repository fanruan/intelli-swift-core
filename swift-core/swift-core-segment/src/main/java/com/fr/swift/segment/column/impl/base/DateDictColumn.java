package com.fr.swift.segment.column.impl.base;

import com.fr.swift.cube.io.IOConstant;
import com.fr.swift.cube.io.input.LongReader;
import com.fr.swift.cube.io.location.IResourceLocation;
import com.fr.swift.cube.io.output.LongWriter;
import com.fr.swift.source.ColumnTypeConstants.ClassType;

import java.util.Comparator;
import java.util.Date;

/**
 * @author anchore
 * @date 2018/10/24
 */
public class DateDictColumn extends BaseDictColumn<Date, LongReader> {

    public DateDictColumn(IResourceLocation parent, Comparator<Date> keyComparator) {
        super(parent, keyComparator);
    }

    @Override
    public Date getValue(int index) {
        return new Date(keyReader.get(index));
    }

    @Override
    public ClassType getType() {
        return ClassType.DATE;
    }

    @Override
    public Putter<Date> putter() {
        return new BasePutter<LongWriter>() {
            @Override
            public void putValue(int index, Date val) {
                keyWriter.put(index, val == null ? IOConstant.NULL_LONG : val.getTime());
            }
        };
    }
}