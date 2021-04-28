package com.fr.swift.cloud.test;

import com.fr.swift.cloud.bitmap.ImmutableBitMap;
import com.fr.swift.cloud.segment.column.BitmapIndexedColumn;
import com.fr.swift.cloud.segment.column.DetailColumn;
import com.fr.swift.cloud.segment.column.DictionaryEncodedColumn;
import com.fr.swift.cloud.source.ColumnTypeConstants;

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
 * @deprecated 换用mock
 */
@Deprecated
public final class Temps {
    @Deprecated
    public static class TempDictColumn<T> implements DictionaryEncodedColumn<T> {
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
        public T getValueByRow(int row) {
            return getValue(getIndexByRow(row));
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
        public int getGlobalIndexByRow(int row) {
            return getGlobalIndexByIndex(getIndexByRow(row));
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
        public void release() {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean isReadable() {
            return false;
        }
    }

    @Deprecated
    public static class TempBitmapIndexedColumn implements BitmapIndexedColumn {

        @Override
        public void putBitMapIndex(int index, ImmutableBitMap bitmap) {

        }

        @Override
        public ImmutableBitMap getBitMapIndex(int index) {
            return null;
        }

        @Override
        public void putNullIndex(ImmutableBitMap bitMap) {

        }

        @Override
        public ImmutableBitMap getNullIndex() {
            return null;
        }

        @Override
        public void release() {

        }

        @Override
        public boolean isReadable() {
            return false;
        }
    }

    @Deprecated
    public static class TempDetailColumn implements DetailColumn {

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
        public void release() {

        }

        @Override
        public boolean isReadable() {
            return false;
        }
    }

}