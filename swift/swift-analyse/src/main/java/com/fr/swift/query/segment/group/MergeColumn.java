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
public class MergeColumn implements IMergeColumn {
    private DictionaryEncodedColumn dic;
    private IntList mergeIndex;
    private List values;

    public MergeColumn(DictionaryEncodedColumn dic, IntList mergeIndex) {
        this.dic = dic;
        this.mergeIndex = mergeIndex;
    }

    @Override
    public int getIndex(int row) {
        return mergeIndex.get(dic.getIndexByRow(row));
    }

    @Override
    public Object getValue(int row) {
        return dic.getValueByRow(row);
    }

    @Override
    public int dictSize() {
        return mergeIndex.size();
    }
}
