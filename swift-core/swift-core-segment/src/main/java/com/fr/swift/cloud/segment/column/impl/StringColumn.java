package com.fr.swift.cloud.segment.column.impl;

import com.fr.swift.cloud.compare.Comparators;
import com.fr.swift.cloud.cube.io.location.IResourceLocation;
import com.fr.swift.cloud.segment.column.DetailColumn;
import com.fr.swift.cloud.segment.column.DictionaryEncodedColumn;
import com.fr.swift.cloud.segment.column.impl.base.StringDetailColumn;
import com.fr.swift.cloud.segment.column.impl.base.StringDictColumn;

/**
 * @author anchore
 * @date 2017/11/30
 */
public class StringColumn extends BaseColumn<String> {
    public StringColumn(IResourceLocation location) {
        super(location);
    }

    @Override
    public DictionaryEncodedColumn<String> getDictionaryEncodedColumn() {
        return dictColumn == null ? dictColumn = new StringDictColumn(location, Comparators.STRING_ASC) : dictColumn;
    }

    @Override
    public DetailColumn<String> getDetailColumn() {
        return detailColumn == null ? detailColumn = new StringDetailColumn(location) : detailColumn;
    }
}