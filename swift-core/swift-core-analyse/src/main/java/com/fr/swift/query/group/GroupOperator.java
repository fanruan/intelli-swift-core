package com.fr.swift.query.group;

import com.fr.swift.segment.column.Column;

import java.util.List;

/**
 * @param <Base>   分组前列类型
 * @param <Derive> 分组后列类型，一般为String
 * @author pony
 * @date 2017/12/7
 */
public interface GroupOperator<Base, Derive> {
    Column<Derive> group(Column<Base> column);

    Column<Derive> group(List<Column<Base>> columnList);

}