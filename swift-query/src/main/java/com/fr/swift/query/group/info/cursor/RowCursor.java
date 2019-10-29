package com.fr.swift.query.group.info.cursor;

import com.fr.swift.segment.column.DictionaryEncodedColumn;

import java.util.Arrays;
import java.util.List;

/**
 * Created by pony on 2017/12/15.
 */
public class RowCursor implements Cursor {

    private List<String> groupValues;

    public RowCursor(List<String> groupValues) {
        this.groupValues = groupValues;
    }

    @Override
    public int[] createCursorIndex(List<DictionaryEncodedColumn> columns) {
        int[] index = new int[columns.size()];
        Arrays.fill(index, 0);
        for (int i = 0; i < groupValues.size(); i++) {
            index[i] = columns.get(i).getIndex(groupValues.get(i));
        }
        return index;
    }
}
