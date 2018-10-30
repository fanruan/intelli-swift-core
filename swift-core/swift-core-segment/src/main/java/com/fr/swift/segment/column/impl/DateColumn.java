package com.fr.swift.segment.column.impl;

import com.fr.swift.compare.Comparators;
import com.fr.swift.cube.io.location.IResourceLocation;
import com.fr.swift.segment.column.DetailColumn;
import com.fr.swift.segment.column.DictionaryEncodedColumn;
import com.fr.swift.segment.column.impl.base.DateDetailColumn;
import com.fr.swift.segment.column.impl.base.DateDictColumn;

import java.util.Date;

/**
 * @author anchore
 * @date 2017/11/30
 */
public class DateColumn extends BaseColumn<Date> {

    public DateColumn(IResourceLocation location) {
        super(location);
    }

    @Override
    public DictionaryEncodedColumn<Date> getDictionaryEncodedColumn() {
        return dictColumn != null ? dictColumn :
                (dictColumn = new DateDictColumn(location, Comparators.<Date>asc()));
    }

    @Override
    public DetailColumn<Date> getDetailColumn() {
        return detailColumn != null ? detailColumn :
                (detailColumn = new DateDetailColumn(location));
    }
}
