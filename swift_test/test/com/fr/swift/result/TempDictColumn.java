package com.fr.swift.result;

import com.fr.swift.segment.column.DictionaryEncodedColumn;

import java.util.Comparator;

public class TempDictColumn implements DictionaryEncodedColumn {
    @Override
    public void putSize(int size) {

    }

    @Override
    public int size() {
        return 0;
    }

    @Override
    public void putGlobalSize(int globalSize) {

    }

    @Override
    public int globalSize() {
        return 0;
    }

    @Override
    public void putValue(int index, Object val) {

    }

    @Override
    public Object getValue(int index) {
        return null;
    }

    @Override
    public int getIndex(Object value) {
        return 0;
    }

    @Override
    public void putIndex(int row, int index) {

    }

    @Override
    public int getIndexByRow(int row) {
        return 0;
    }

    @Override
    public void putGlobalIndex(int index, int globalIndex) {

    }

    @Override
    public int getGlobalIndexByIndex(int index) {
        return 0;
    }

    @Override
    public int getGlobalIndexByRow(int row) {
        return 0;
    }

    @Override
    public Comparator getComparator() {
        return null;
    }

    @Override
    public Object convertValue(Object value) {
        return null;
    }

    @Override
    public void flush() {

    }

    @Override
    public void release() {

    }
}
