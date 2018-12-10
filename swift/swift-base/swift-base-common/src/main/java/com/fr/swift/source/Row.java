package com.fr.swift.source;

/**
 * Created by pony on 2017/10/25.
 * 表示一行数据
 */
public interface Row {
    <V> V getValue(int index);

    int getSize();
}
