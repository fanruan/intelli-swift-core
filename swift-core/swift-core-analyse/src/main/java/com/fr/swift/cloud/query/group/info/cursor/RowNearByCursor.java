package com.fr.swift.cloud.query.group.info.cursor;

import com.fr.swift.cloud.segment.column.DictionaryEncodedColumn;

import java.util.List;

/**
 * Created by pony on 2017/12/15.
 */
public class RowNearByCursor implements Cursor {

    @Override
    public int[] createCursorIndex(List<DictionaryEncodedColumn> columns) {
        return new int[0];
    }
}
