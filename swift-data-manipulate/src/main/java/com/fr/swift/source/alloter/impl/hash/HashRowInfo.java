package com.fr.swift.source.alloter.impl.hash;

import com.fr.swift.source.Row;
import com.fr.swift.source.alloter.RowInfo;

/**
 * @author anchore
 * @date 2018/6/5
 */
public class HashRowInfo implements RowInfo {

    private Row row;

    public HashRowInfo(Row row) {
        this.row = row;
    }

    Row getRow() {
        return row;
    }
}