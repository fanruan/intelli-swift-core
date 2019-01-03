package com.fr.swift.source;

import com.fr.swift.base.json.annotation.JsonTypeInfo;

/**
 * Created by pony on 2017/10/25.
 * 表示一行数据
 */
@JsonTypeInfo(
        defaultImpl = ListBasedRow.class
)
public interface Row {
    <V> V getValue(int index);

    int getSize();
}
