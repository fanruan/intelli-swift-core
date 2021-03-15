package com.fr.swift.cloud.segment.column.impl;

import com.fr.swift.cloud.compare.Comparators;
import com.fr.swift.cloud.cube.io.location.IResourceLocation;
import com.fr.swift.cloud.segment.column.DetailColumn;
import com.fr.swift.cloud.segment.column.DictionaryEncodedColumn;
import com.fr.swift.cloud.segment.column.impl.base.IntDetailColumn;
import com.fr.swift.cloud.segment.column.impl.base.IntDictColumn;

/**
 * @author anchore
 * @date 2017/11/30
 */
public class IntColumn extends BaseColumn<Integer> {
    public IntColumn(IResourceLocation location) {
        super(location);
    }

    @Override
    public DictionaryEncodedColumn<Integer> getDictionaryEncodedColumn() {
        return dictColumn == null ? dictColumn = new IntDictColumn(location, Comparators.<Integer>asc()) : dictColumn;
    }

    @Override
    public DetailColumn<Integer> getDetailColumn() {
        return detailColumn == null ? detailColumn = new IntDetailColumn(location) : detailColumn;
    }
}