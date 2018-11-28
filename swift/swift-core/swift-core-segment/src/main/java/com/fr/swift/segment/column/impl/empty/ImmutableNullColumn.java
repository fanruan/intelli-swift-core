package com.fr.swift.segment.column.impl.empty;

import com.fr.swift.bitmap.ImmutableBitMap;
import com.fr.swift.bitmap.impl.RangeBitmap;
import com.fr.swift.compare.Comparators;
import com.fr.swift.cube.io.IOConstant;
import com.fr.swift.cube.io.location.IResourceLocation;
import com.fr.swift.segment.column.BitmapIndexedColumn;
import com.fr.swift.segment.column.Column;
import com.fr.swift.segment.column.DetailColumn;
import com.fr.swift.segment.column.DictionaryEncodedColumn;
import com.fr.swift.segment.column.impl.base.AbstractDictColumn;
import com.fr.swift.segment.column.impl.base.BaseBitmapColumn;
import com.fr.swift.source.ColumnTypeConstants.ClassType;

import java.util.Comparator;

/**
 * @author anchore
 * @date 2018/8/16
 */
public abstract class ImmutableNullColumn<T> implements Column<T> {
    private IResourceLocation location;

    private int rowCount;

    public ImmutableNullColumn(IResourceLocation location, int rowCount) {
        this.location = location;
        this.rowCount = rowCount;
    }

    public static Column<Long> ofLong(IResourceLocation location, int rowCount) {
        return new ImmutableNullColumn<Long>(location, rowCount) {
            @Override
            public DictionaryEncodedColumn<Long> getDictionaryEncodedColumn() {
                return new BaseNullDictColumn() {
                    @Override
                    public Comparator<Long> getComparator() {
                        return Comparators.asc();
                    }

                    @Override
                    public ClassType getType() {
                        return ClassType.LONG;
                    }
                };
            }
        };
    }

    public static Column<Double> ofDouble(IResourceLocation location, int rowCount) {
        return new ImmutableNullColumn<Double>(location, rowCount) {
            @Override
            public DictionaryEncodedColumn<Double> getDictionaryEncodedColumn() {
                return new BaseNullDictColumn() {
                    @Override
                    public Comparator<Double> getComparator() {
                        return Comparators.asc();
                    }

                    @Override
                    public ClassType getType() {
                        return ClassType.DOUBLE;
                    }
                };
            }
        };
    }

    public static Column<String> ofString(IResourceLocation location, int rowCount) {
        return new ImmutableNullColumn<String>(location, rowCount) {
            @Override
            public DictionaryEncodedColumn<String> getDictionaryEncodedColumn() {
                return new BaseNullDictColumn() {
                    @Override
                    public Comparator<String> getComparator() {
                        return Comparators.STRING_ASC;
                    }

                    @Override
                    public ClassType getType() {
                        return ClassType.STRING;
                    }
                };
            }
        };
    }

    abstract class BaseNullDictColumn extends AbstractDictColumn<T> {
        @Override
        public int size() {
            return 1;
        }

        @Override
        public int globalSize() {
            return 1;
        }

        @Override
        public T getValue(int index) {
            checkIndex(index, size());
            return null;
        }

        @Override
        public int getIndex(Object value) {
            if (value == null) {
                return 0;
            }
            return -1;
        }

        @Override
        public int getIndexByRow(int row) {
            checkIndex(row, rowCount);
            return 0;
        }

        @Override
        public int getGlobalIndexByIndex(int index) {
            checkIndex(index, size());
            return 0;
        }

        @Override
        public Putter<T> putter() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void flush() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void release() {
        }

        @Override
        public boolean isReadable() {
            return true;
        }
    }

    @Override
    public BitmapIndexedColumn getBitmapIndex() {
        return new BaseBitmapColumn() {
            @Override
            public boolean isReadable() {
                return true;
            }

            @Override
            public void putBitMapIndex(int index, ImmutableBitMap bitmap) {
                throw new UnsupportedOperationException();
            }

            @Override
            public ImmutableBitMap getBitMapIndex(int index) {
                return new RangeBitmap(0, rowCount);
            }

            @Override
            public void release() {
            }
        };
    }

    @Override
    public DetailColumn<T> getDetailColumn() {
        return new DetailColumn<T>() {
            @Override
            public int getInt(int pos) {
                checkIndex(pos, rowCount);
                return IOConstant.NULL_INT;
            }

            @Override
            public long getLong(int pos) {
                checkIndex(pos, rowCount);
                return IOConstant.NULL_LONG;
            }

            @Override
            public double getDouble(int pos) {
                checkIndex(pos, rowCount);
                return IOConstant.NULL_DOUBLE;
            }

            @Override
            public void put(int pos, T val) {
                throw new UnsupportedOperationException();
            }

            @Override
            public T get(int pos) {
                checkIndex(pos, rowCount);
                return null;
            }

            @Override
            public boolean isReadable() {
                return true;
            }

            @Override
            public void flush() {
                throw new UnsupportedOperationException();
            }

            @Override
            public void release() {
            }
        };
    }

    private static void checkIndex(int index, int size) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException(String.format("index: %d, size: %d", index, size));
        }
    }

    @Override
    public IResourceLocation getLocation() {
        return location;
    }
}