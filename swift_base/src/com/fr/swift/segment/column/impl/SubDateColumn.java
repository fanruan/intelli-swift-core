package com.fr.swift.segment.column.impl;

import com.fr.swift.bitmap.ImmutableBitMap;
import com.fr.swift.compare.Comparators;
import com.fr.swift.query.group.GroupType;
import com.fr.swift.segment.column.BitmapIndexedColumn;
import com.fr.swift.segment.column.Column;
import com.fr.swift.segment.column.DetailColumn;
import com.fr.swift.segment.column.DictionaryEncodedColumn;
import com.fr.swift.segment.column.impl.base.IntDictColumn;
import com.fr.swift.segment.column.impl.base.LongDictColumn;
import com.fr.swift.util.function.Function;

/**
 * 日期子列，用于分组的
 *
 * @param <Derive> 新值
 * @author anchore
 * @date 2017/12/4
 * @see GroupType
 */
public class SubDateColumn<Derive> extends BaseColumn<Derive> {
    public static final GroupType[] TYPES_TO_GENERATE = {
            GroupType.YEAR, GroupType.QUARTER, GroupType.MONTH,
            GroupType.Y_M_D, GroupType.Y_M
    };

    private GroupType type;

    /**
     * 源列，子列的父列
     */
    private Column<Long> origin;

    public SubDateColumn(Column<Long> origin, GroupType type) {
        super(origin.getLocation().buildChildLocation(type.toString()));
        this.type = type;
        this.origin = origin;
    }

    @Override
    public DetailColumn<Derive> getDetailColumn() throws UnsupportedOperationException {
        return new SubDetailColumn();
    }

    @Override
    public DictionaryEncodedColumn<Derive> getDictionaryEncodedColumn() {
        switch (type) {
            case YEAR:
            case QUARTER:
            case MONTH:
            case WEEK:
            case DAY:
            case HOUR:
            case MINUTE:
            case SECOND:
            case WEEK_OF_YEAR:
                return (DictionaryEncodedColumn<Derive>) new IntDictColumn(location, Comparators.<Integer>asc());
            default:
                return (DictionaryEncodedColumn<Derive>) new LongDictColumn(location, Comparators.<Long>asc());
        }
    }

    @Override
    public BitmapIndexedColumn getBitmapIndex() {
        return new SubBitmapIndexedColumn();
    }

    private class SubDetailColumn implements DetailColumn<Derive> {
        private DetailColumn<Long> baseDetail = origin.getDetailColumn();

        private Function<Long, Derive> deriver = DateDerivers.newDeriver(type);

        @Override
        public int getInt(int pos) {
            throw new UnsupportedOperationException();
        }

        @Override
        public long getLong(int pos) {
            throw new UnsupportedOperationException();
        }

        @Override
        public double getDouble(int pos) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void put(int pos, Derive val) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Derive get(int pos) {
            return deriver.apply(baseDetail.get(pos));
        }

        @Override
        public void release() {
            baseDetail.release();
        }

        @Override
        public void flush() {
            baseDetail.flush();
        }
    }

    private class SubBitmapIndexedColumn implements BitmapIndexedColumn {
        BitmapIndexedColumn deriveBitmapColumn = SubDateColumn.super.getBitmapIndex();

        @Override
        public void putBitMapIndex(int index, ImmutableBitMap bitmap) {
            deriveBitmapColumn.putBitMapIndex(index, bitmap);
        }

        @Override
        public ImmutableBitMap getBitMapIndex(int index) {
            if (index < 1) {
                // 拿源列的nullIndex
                return getNullIndex();
            }
            return deriveBitmapColumn.getBitMapIndex(index);
        }

        @Override
        public void putNullIndex(ImmutableBitMap bitMap) {
            throw new UnsupportedOperationException();
        }

        @Override
        public ImmutableBitMap getNullIndex() {
            return origin.getBitmapIndex().getNullIndex();
        }

        @Override
        public void flush() {
            deriveBitmapColumn.flush();
        }

        @Override
        public void release() {
            deriveBitmapColumn.release();
        }
    }
}