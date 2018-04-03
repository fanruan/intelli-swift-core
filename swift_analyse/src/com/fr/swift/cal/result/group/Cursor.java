package com.fr.swift.cal.result.group;

import com.fr.swift.segment.column.DictionaryEncodedColumn;

import java.util.List;

/**
 * Created by pony on 2017/12/15.
 */
public interface Cursor {

    int[] createCursorIndex(List<DictionaryEncodedColumn> columns);
}
