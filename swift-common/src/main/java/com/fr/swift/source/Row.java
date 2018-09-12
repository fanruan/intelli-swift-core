package com.fr.swift.source;

import java.io.Serializable;

/**
 * Created by pony on 2017/10/25.
 * 表示一行数据
 */
public interface Row extends Serializable {
    <V> V getValue(int index);

    int getSize();
}
