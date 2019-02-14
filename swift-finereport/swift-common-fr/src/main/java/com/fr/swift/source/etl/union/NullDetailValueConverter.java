package com.fr.swift.source.etl.union;

/**
 * Created by pony on 2018/3/28.
 */
public class NullDetailValueConverter implements ColumnDetailValueConverter {
    @Override
    public Object convertValue(int row) {
        return null;
    }
}
