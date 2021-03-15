package com.fr.swift.cloud.query.group.info.cursor;

import com.fr.swift.cloud.segment.column.DictionaryEncodedColumn;

import java.util.Arrays;
import java.util.List;

/**
 * Created by pony on 2017/12/15.
 */
public class AllCursor implements Cursor {

    @Override
    public int[] createCursorIndex(List<DictionaryEncodedColumn> columns) {
        int[] index = new int[columns.size()];
        Arrays.fill(index, 0);
        return index;
    }
}
