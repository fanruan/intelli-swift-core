package com.fr.swift.cloud.source.alloter.impl.hash;

import com.fr.swift.cloud.source.Row;
import com.fr.swift.cloud.source.alloter.RowInfo;

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