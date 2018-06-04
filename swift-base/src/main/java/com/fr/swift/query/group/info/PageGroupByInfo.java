package com.fr.swift.query.group.info;

import com.fr.swift.query.group.info.cursor.Cursor;

/**
 * Created by Lyon on 2018/4/25.
 */
public interface PageGroupByInfo extends GroupByInfo {

    /**
     * 分页游标
     */
    Cursor getCursor();
}
