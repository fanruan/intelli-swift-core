package com.fr.swift.segment.column.impl;

import com.fr.swift.compare.Comparators;
import com.fr.swift.cube.io.location.IResourceLocation;
import com.fr.swift.segment.column.DetailColumn;
import com.fr.swift.segment.column.DictionaryEncodedColumn;
import com.fr.swift.segment.column.impl.base.LongDetailColumn;
import com.fr.swift.segment.column.impl.base.LongDictColumn;

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
        return dictColumn != null ? dictColumn : (dictColumn = new LongDictColumn(location, Comparators.<Long>asc()));
    }

    @Override
    public DetailColumn<Long> getDetailColumn() {
        return detailColumn != null ? detailColumn : (detailColumn = new LongDetailColumn(location));
    }
}