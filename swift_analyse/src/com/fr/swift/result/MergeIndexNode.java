package com.fr.swift.result;

import com.fr.swift.segment.column.DictionaryEncodedColumn;

/**
 * Created by pony on 2017/12/8.
 */
public class MergeIndexNode extends IndexNode {
    private int index;
    private int dicIndex;
    private DictionaryEncodedColumn[] dictionaryEncodedColumn;

    public MergeIndexNode(int sumLength, int index, int dicIndex, DictionaryEncodedColumn[] dictionaryEncodedColumn) {
        super(sumLength);
        this.index = index;
        this.dicIndex = dicIndex;
        this.dictionaryEncodedColumn = dictionaryEncodedColumn;
    }

    @Override
    protected void initDataByIndex() {
        data = dictionaryEncodedColumn[dicIndex].getValue(index);
    }

    @Override
    protected Object createKey() {
        return ((long)dicIndex << 31) + index;
    }

    @Override
    public int getDeep() {
        return 0;
    }
}
