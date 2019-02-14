package com.fr.swift.segment.column.impl;

import com.fr.swift.compare.Comparators;
import com.fr.swift.cube.io.location.IResourceLocation;
import com.fr.swift.segment.column.DetailColumn;
import com.fr.swift.segment.column.DictionaryEncodedColumn;
import com.fr.swift.segment.column.impl.base.StringDetailColumn;
import com.fr.swift.segment.column.impl.base.StringDictColumn;

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
        return dictColumn != null ? dictColumn : (dictColumn = new StringDictColumn(location, Comparators.STRING_ASC));
    }

    @Override
    public DetailColumn<String> getDetailColumn() {
        return detailColumn != null ? detailColumn : (detailColumn = new StringDetailColumn(location));
    }
}