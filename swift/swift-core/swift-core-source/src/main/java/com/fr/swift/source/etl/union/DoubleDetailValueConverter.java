package com.fr.swift.source.etl.union;

import com.fr.swift.segment.column.DictionaryEncodedColumn;

/**
 * Created by pony on 2018/3/28.
 */
public class DoubleDetailValueConverter implements ColumnDetailValueConverter<Double> {
    private DictionaryEncodedColumn dic;

    public DoubleDetailValueConverter(DictionaryEncodedColumn dic) {
        this.dic = dic;
    }

    @Override
    public Double convertValue(int row) {
        Object ob = dic.getValue(dic.getIndexByRow(row));
        return ob == null ? null : ((Number) ob).doubleValue();
    }
}
