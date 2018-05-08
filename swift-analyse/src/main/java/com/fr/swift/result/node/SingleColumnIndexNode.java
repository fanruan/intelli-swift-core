package com.fr.swift.result.node;

import com.fr.swift.segment.column.DictionaryEncodedColumn;

/**
 * Created by pony on 2017/12/8.
 */
public class SingleColumnIndexNode extends IndexNode {
    private int index;
    private DictionaryEncodedColumn dictionaryEncodedColumn;

    public SingleColumnIndexNode(int sumLength, int index, DictionaryEncodedColumn dictionaryEncodedColumn) {
        super(sumLength);
        this.index = index;
        this.dictionaryEncodedColumn = dictionaryEncodedColumn;
    }

    protected void initDataByIndex() {
        data = dictionaryEncodedColumn.getValue(index);
    }

    protected Object createKey() {
        return index;
    }

    @Override
    public int getDepth() {
        return 0;
    }
}
