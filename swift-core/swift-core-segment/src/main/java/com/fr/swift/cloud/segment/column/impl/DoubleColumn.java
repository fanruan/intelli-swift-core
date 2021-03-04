package com.fr.swift.cloud.segment.column.impl;

import com.fr.swift.cloud.compare.Comparators;
import com.fr.swift.cloud.cube.io.location.IResourceLocation;
import com.fr.swift.cloud.segment.column.DetailColumn;
import com.fr.swift.cloud.segment.column.DictionaryEncodedColumn;
import com.fr.swift.cloud.segment.column.impl.base.DoubleDetailColumn;
import com.fr.swift.cloud.segment.column.impl.base.DoubleDictColumn;

/**
 * @author anchore
 * @date 2017/11/30
 */
public class DoubleColumn extends BaseColumn<Double> {
    public DoubleColumn(IResourceLocation location) {
        super(location);
    }

    @Override
    public DictionaryEncodedColumn<Double> getDictionaryEncodedColumn() {
        return dictColumn == null ? dictColumn = new DoubleDictColumn(location, Comparators.<Double>asc()) : dictColumn;
    }

    @Override
    public DetailColumn<Double> getDetailColumn() {
        return detailColumn == null ? detailColumn = new DoubleDetailColumn(location) : detailColumn;
    }
}