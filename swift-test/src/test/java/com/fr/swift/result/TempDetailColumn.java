package com.fr.swift.result;

import com.fr.swift.segment.column.DetailColumn;

public class TempDetailColumn implements DetailColumn {
    @Override
    public int getInt(int pos) {
        return 0;
    }

    @Override
    public long getLong(int pos) {
        return 0;
    }

    @Override
    public double getDouble(int pos) {
        return 0;
    }

    @Override
    public void put(int pos, Object val) {

    }

    @Override
    public Object get(int pos) {
        return null;
    }

    @Override
    public void flush() {

    }

    @Override
    public void release() {

    }
}
