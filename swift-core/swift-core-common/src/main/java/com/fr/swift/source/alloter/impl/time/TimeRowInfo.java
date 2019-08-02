package com.fr.swift.source.alloter.impl.time;

import com.fr.swift.source.Row;
import com.fr.swift.source.alloter.impl.hash.HashRowInfo;

/**
 * @author Marvin
 * @date 8/2/2019
 * @description
 * @since swift 1.1
 */
public class TimeRowInfo extends HashRowInfo {

    private Row row;

    public TimeRowInfo(Row row) {
        super(row);
    }

    Row getRow() {
        return row;
    }
}
