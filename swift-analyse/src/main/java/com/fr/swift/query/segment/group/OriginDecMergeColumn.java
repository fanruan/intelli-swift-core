package com.fr.swift.query.segment.group;

import com.fr.swift.segment.column.DictionaryEncodedColumn;

/**
 * This class created on 2018/12/13
 *
 * @author Lucifer
 * @description
 */
public class OriginDecMergeColumn implements IMergeColumn {
    private DictionaryEncodedColumn dic;

    public OriginDecMergeColumn(DictionaryEncodedColumn dic) {
        this.dic = dic;
    }

    @Override
    public int getIndex(int row) {
        return dic.getIndexByRow(row);
    }

    @Override
    public Object getValue(int row) {
        return dic.getValueByRow(row);
    }

    @Override
    public int dictSize() {
        return dic.size();
    }
}
