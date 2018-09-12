package com.fr.swift.source.etl.union;

/**
 * Created by pony on 2018/3/28.
 */
public interface ColumnDetailValueConverter<T> {
    /**
     * 将第几行的值转化成新的类型
     *
     * @param row
     * @return
     */
    T convertValue(int row);
}
