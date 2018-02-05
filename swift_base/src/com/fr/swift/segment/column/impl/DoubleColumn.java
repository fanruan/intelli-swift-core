package com.fr.swift.segment.column.impl;

import com.fr.swift.compare.Comparators;
import com.fr.swift.cube.io.location.IResourceLocation;
import com.fr.swift.segment.column.DictionaryEncodedColumn;
import com.fr.swift.segment.column.impl.base.DoubleDetailColumn;
import com.fr.swift.segment.column.impl.base.DoubleDictColumn;

import java.util.Comparator;

/**
 * @author anchore
 * @date 2017/11/30
 */
public class DoubleColumn extends BaseColumn<Double> {
    private DoubleDetailColumn detailColumn;

    public DoubleColumn(IResourceLocation location) {
        super(location);
    }

    @Override
    public DictionaryEncodedColumn<Double> getDictionaryEncodedColumn() {
        return dictColumn != null ? dictColumn : (dictColumn = new DoubleDictColumn(location, Comparators.<Double>asc()));
    }

    @Override
    public DoubleDetailColumn getDetailColumn() {
        return detailColumn != null ? detailColumn : (detailColumn = new DoubleDetailColumn(location));
    }
}