package com.fr.swift.segment.column.impl.empty;

import com.fr.swift.compare.Comparators;
import com.fr.swift.cube.io.location.IResourceLocation;
import com.fr.swift.segment.column.BitmapIndexedColumn;
import com.fr.swift.segment.column.Column;
import com.fr.swift.segment.column.DetailColumn;
import com.fr.swift.segment.column.DictionaryEncodedColumn;
import com.fr.swift.source.ColumnTypeConstants.ClassType;

/**
 * @author anchore
 * @date 2018/8/16
 */
public abstract class ReadonlyNullColumn<T> implements Column<T> {

    private IResourceLocation location;

    int rowCount;

    public ReadonlyNullColumn(IResourceLocation location, int rowCount) {
        this.location = location;
        this.rowCount = rowCount;
    }

    public static Column<Integer> ofInt(IResourceLocation location, int rowCount) {
        return new ReadonlyNullColumn<Integer>(location, rowCount) {
            @Override
            public DictionaryEncodedColumn<Integer> getDictionaryEncodedColumn() {
                return new ReadonlyNullDictColumn<Integer>(rowCount, Comparators.<Integer>asc(), ClassType.INTEGER);
            }
        };
    }

    public static Column<Long> ofLong(IResourceLocation location, int rowCount) {
        return new ReadonlyNullColumn<Long>(location, rowCount) {
            @Override
            public DictionaryEncodedColumn<Long> getDictionaryEncodedColumn() {
                return new ReadonlyNullDictColumn<Long>(rowCount, Comparators.<Long>asc(), ClassType.LONG);
            }
        };
    }

    public static Column<Double> ofDouble(IResourceLocation location, int rowCount) {
        return new ReadonlyNullColumn<Double>(location, rowCount) {
            @Override
            public DictionaryEncodedColumn<Double> getDictionaryEncodedColumn() {
                return new ReadonlyNullDictColumn<Double>(rowCount, Comparators.<Double>asc(), ClassType.DOUBLE);
            }
        };
    }

    public static Column<String> ofString(IResourceLocation location, int rowCount) {
        return new ReadonlyNullColumn<String>(location, rowCount) {
            @Override
            public DictionaryEncodedColumn<String> getDictionaryEncodedColumn() {
                return new ReadonlyNullDictColumn<String>(rowCount, Comparators.STRING_ASC, ClassType.STRING);
            }
        };
    }

    @Override
    public BitmapIndexedColumn getBitmapIndex() {
        return new ReadonlyNullBitmapColumn(rowCount);
    }

    @Override
    public DetailColumn<T> getDetailColumn() {
        return new ReadonlyNullDetailColumn<T>(rowCount);
    }

    static void checkIndex(int index, int size) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException(String.format("index: %d, size: %d", index, size));
        }
    }

    @Override
    public IResourceLocation getLocation() {
        return location;
    }
}