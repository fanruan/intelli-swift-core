package com.fr.swift.util;

import com.fr.swift.bitmap.ImmutableBitMap;
import com.fr.swift.segment.column.BitmapIndexedColumn;
import com.fr.swift.segment.column.DictionaryEncodedColumn;

import java.util.Comparator;

/**
 * @author anchore
 * @date 2018/3/19
 */
public class Extends {
    public static class ExtendsDictColumn<T> implements DictionaryEncodedColumn<T> {
        @Override
        public void putSize(int size) {
            throw new UnsupportedOperationException();
        }

        @Override
        public int size() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void putGlobalSize(int globalSize) {
            throw new UnsupportedOperationException();
        }

        @Override
        public int globalSize() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void putValue(int index, T val) {
            throw new UnsupportedOperationException();
        }

        @Override
        public T getValue(int index) {
            throw new UnsupportedOperationException();
        }

        @Override
        public int getIndex(Object value) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void putIndex(int row, int index) {
            throw new UnsupportedOperationException();
        }

        @Override
        public int getIndexByRow(int row) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void putGlobalIndex(int index, int globalIndex) {
            throw new UnsupportedOperationException();
        }

        @Override
        public int getGlobalIndexByIndex(int index) {
            throw new UnsupportedOperationException();
        }

        @Override
        public int getGlobalIndexByRow(int row) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Comparator<T> getComparator() {
            throw new UnsupportedOperationException();
        }

        @Override
        public T convertValue(Object value) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void flush() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void release() {
            throw new UnsupportedOperationException();
        }
    }

    public static class ExtendsBitmapColumn implements BitmapIndexedColumn {
        @Override
        public void putBitMapIndex(int index, ImmutableBitMap bitmap) {
            throw new UnsupportedOperationException();
        }

        @Override
        public ImmutableBitMap getBitMapIndex(int index) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void putNullIndex(ImmutableBitMap bitMap) {
            throw new UnsupportedOperationException();
        }

        @Override
        public ImmutableBitMap getNullIndex() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void flush() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void release() {
            throw new UnsupportedOperationException();
        }
    }
}