package com.fr.swift.query.segment.group;

import com.fr.swift.segment.column.DetailColumn;

/**
 * This class created on 2018/12/13
 *
 * @author Lucifer
 * @description
 */
public class DetailOrigineColumn implements IMergeColumn {
    private DetailColumn column;

    public DetailOrigineColumn(DetailColumn column) {
        this.column = column;
    }

    @Override
    public int getIndex(int row) {
        return 0;
    }

    @Override
    public Object getValue(int row) {
        return column.get(row);
    }

    @Override
    public int dictSize() {
        throw new UnsupportedOperationException();
    }
}
