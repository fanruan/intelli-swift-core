package com.fr.swift.query.group.info.cursor;

import com.fr.swift.segment.column.DictionaryEncodedColumn;

import java.util.List;

/**
 * Created by pony on 2017/12/15.
 */
public interface Cursor {

    int[] createCursorIndex(List<DictionaryEncodedColumn> columns);
}
