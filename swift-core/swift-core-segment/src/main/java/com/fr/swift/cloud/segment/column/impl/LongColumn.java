package com.fr.swift.cloud.segment.column.impl;

import com.fr.swift.cloud.compare.Comparators;
import com.fr.swift.cloud.cube.io.location.IResourceLocation;
import com.fr.swift.cloud.segment.column.DetailColumn;
import com.fr.swift.cloud.segment.column.DictionaryEncodedColumn;
import com.fr.swift.cloud.segment.column.impl.base.LongDetailColumn;
import com.fr.swift.cloud.segment.column.impl.base.LongDictColumn;

/**
 * @author anchore
 * @date 2017/11/30
 */
public class LongColumn extends BaseColumn<Long> {
    public LongColumn(IResourceLocation location) {
        super(location);
    }

    @Override
    public DictionaryEncodedColumn<Long> getDictionaryEncodedColumn() {
        return dictColumn == null ? dictColumn = new LongDictColumn(location, Comparators.<Long>asc()) : dictColumn;
    }

    @Override
    public DetailColumn<Long> getDetailColumn() {
        return detailColumn == null ? detailColumn = new LongDetailColumn(location) : detailColumn;
    }
}