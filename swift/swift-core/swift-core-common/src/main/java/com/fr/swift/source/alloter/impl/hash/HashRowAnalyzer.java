package com.fr.swift.source.alloter.impl.hash;

import com.fr.swift.source.alloter.RowAnalyzer;
import com.fr.swift.source.alloter.RowInfo;

/**
 * Created by lyon on 2018/12/28.
 */
public class HashRowAnalyzer implements RowAnalyzer {

    private int fieldIndex;
    private int segCount;

    public HashRowAnalyzer(int fieldIndex, int segCount) {
        this.fieldIndex = fieldIndex;
        this.segCount = segCount;
    }

    @Override
    public int analyseHistory(RowInfo row) {
        return Math.abs(((HashRowInfo) row).getRow().getValue(fieldIndex).hashCode() % segCount);
    }

    @Override
    public int analyseRealTime(RowInfo row) {
        return 0;
    }

    @Override
    public int analyseCollate(RowInfo row) {
        return 0;
    }
}
