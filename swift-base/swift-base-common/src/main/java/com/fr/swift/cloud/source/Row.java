package com.fr.swift.cloud.source;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * Created by pony on 2017/10/25.
 * 表示一行数据
 */
@JsonTypeInfo(
        use = JsonTypeInfo.Id.CLASS, defaultImpl = ListBasedRow.class)
public interface Row {
    <V> V getValue(int index);

    int getSize();
}
