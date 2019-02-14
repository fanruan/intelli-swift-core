package com.fr.swift.query.segment.group;

import com.fr.swift.segment.column.DictionaryEncodedColumn;
import com.fr.swift.structure.array.IntList;

import java.util.List;

/**
 * This class created on 2018/12/13
 *
 * @author Lucifer
 * @description
 */
public class ValueMergeColumn extends MergeColumn {
    private List values;

    public ValueMergeColumn(DictionaryEncodedColumn dic, IntList mergeIndex, List values) {
        super(dic, mergeIndex);
        this.values = values;
    }

    @Override
    public Object getValue(int row) {
        return values.get(getIndex(row));
    }

    @Override
    public int dictSize() {
        return values.size();
    }
}
