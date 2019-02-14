package com.fr.swift.source.alloter.impl.line;

import com.fr.swift.source.alloter.RowInfo;

/**
 * @author anchore
 * @date 2018/6/5
 */
public class LineRowInfo implements RowInfo {
    private long cursor;

    public LineRowInfo(long cursor) {
        this.cursor = cursor;
    }

    public long getCursor() {
        return cursor;
    }
}