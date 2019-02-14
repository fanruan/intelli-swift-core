package com.fr.swift.source.etl.union;

import com.fr.swift.segment.column.DictionaryEncodedColumn;

/**
 * Created by pony on 2018/3/28.
 */
public class LongDetailValueConverter implements ColumnDetailValueConverter<Long> {
    private DictionaryEncodedColumn dic;

    public LongDetailValueConverter(DictionaryEncodedColumn dic) {
        this.dic = dic;
    }

    @Override
    public Long convertValue(int row) {
        Object ob = dic.getValue(dic.getIndexByRow(row));
        return ob == null ? null : ((Number) ob).longValue();
    }
}
