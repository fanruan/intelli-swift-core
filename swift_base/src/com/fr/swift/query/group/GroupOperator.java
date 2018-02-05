package com.fr.swift.query.group;

import com.fr.swift.segment.column.Column;

/**
 * Created by pony on 2017/12/7.
 */
public interface GroupOperator {
    Column<String> group(Column<?> column);
}
