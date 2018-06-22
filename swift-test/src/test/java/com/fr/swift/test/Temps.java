package com.fr.swift.test;

import com.fr.swift.segment.column.DictionaryEncodedColumn;

import java.util.Comparator;

/**
 * @author anchore
 * @date 2018/3/12
 * <p>
 * 临时类，测试类继承用
 * 主要是避免底层接口更改，导致多处代码修改
 * 写一个临时类，把修改点缩小到一个类
 * 也避免重复代码，很多时候只需要调用到部分方法，其他方法没用到
 * <p>
 * 正式开发还是不要用，不然堆栈看不懂
 */
public final class Temps {
    public static class TempDictColumn<T> implements DictionaryEncodedColumn<T> {
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
        public T getValueByRow(int row) {
            return getValue(getIndexByRow(row));
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
        public void flush() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void release() {
            throw new UnsupportedOperationException();
        }
    }
}