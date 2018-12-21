package com.fr.swift.source.alloter.impl.hash;

import com.fr.swift.source.Row;
import com.fr.swift.source.alloter.impl.line.LineRowInfo;

/**
 * @author anchore
 * @date 2018/6/5
 */
public class HashRowInfo extends LineRowInfo {

    private Row row;

    public HashRowInfo(long cursor, Row row) {
        super(cursor);
        this.row = row;
    }

    Row getRow() {
        return row;
    }
}