package com.fr.swift.source.etl.union;

import com.fr.swift.segment.column.DictionaryEncodedColumn;

/**
 * Created by pony on 2018/3/28.
 */
public class OriginColumnDetailValueConverter implements ColumnDetailValueConverter<Object> {
    private DictionaryEncodedColumn dic;

    public OriginColumnDetailValueConverter(DictionaryEncodedColumn dic) {
        this.dic = dic;
    }

    @Override
    public Object convertValue(int row) {
        return dic.getValue(dic.getIndexByRow(row));
    }
}
