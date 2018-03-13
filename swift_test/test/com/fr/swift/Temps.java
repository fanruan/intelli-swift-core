package com.fr.swift;

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
 */
public final class Temps {
    public static class TempDictColumn<T> implements DictionaryEncodedColumn<T> {
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
        public void putValue(int index, T val) {
        }

        @Override
        public T getValue(int index) {
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
        public Comparator<T> getComparator() {
            return null;
        }

        @Override
        public T convertValue(Object value) {
            return null;
        }

        @Override
        public void flush() {
        }

        @Override
        public void release() {
        }
    }
}