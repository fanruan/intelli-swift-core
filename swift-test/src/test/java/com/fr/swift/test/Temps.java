package com.fr.swift.test;

import com.fr.swift.segment.column.impl.base.AbstractDictColumn;
import com.fr.swift.source.ColumnTypeConstants;

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
    public static class TempDictColumn<T> extends AbstractDictColumn<T> {
        @Override
        public int size() {
            throw new UnsupportedOperationException();
        }

        @Override
        public int globalSize() {
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
        public int getIndexByRow(int row) {
            throw new UnsupportedOperationException();
        }

        @Override
        public int getGlobalIndexByIndex(int index) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Comparator<T> getComparator() {
            throw new UnsupportedOperationException();
        }

        @Override
        public ColumnTypeConstants.ClassType getType() {
            Object object = getValue(1);
            if (object == null) {
                return null;
            } else if (object instanceof Integer) {
                return ColumnTypeConstants.ClassType.INTEGER;
            } else if (object instanceof Long) {
                return ColumnTypeConstants.ClassType.LONG;
            } else if (object instanceof Double) {
                return ColumnTypeConstants.ClassType.DOUBLE;
            } else {
                return ColumnTypeConstants.ClassType.STRING;
            }
        }

        @Override
        public Putter<T> putter() {
            return null;
        }

        @Override
        public void flush() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void release() {
            throw new UnsupportedOperationException();
        }

        public class TempPutter implements Putter<T> {
            @Override
            public void putSize(int size) {
            }

            @Override
            public void putGlobalSize(int globalSize) {
            }

            @Override
            public void putValue(int index, T val) {
            }

            @Override
            public void putIndex(int row, int index) {
            }

            @Override
            public void putGlobalIndex(int index, int globalIndex) {
            }

            @Override
            public void release() {
            }
        }
    }
}