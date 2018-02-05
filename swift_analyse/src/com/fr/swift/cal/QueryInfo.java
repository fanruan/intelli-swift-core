package com.fr.swift.cal;

import com.fr.swift.cal.builder.QueryType;
import com.fr.swift.cal.result.group.Cursor;

/**
 * Created by pony on 2017/12/12.
 */
public interface QueryInfo<T> {
    QueryType getType();

    Cursor getCursor();

    String getQueryID();
}
