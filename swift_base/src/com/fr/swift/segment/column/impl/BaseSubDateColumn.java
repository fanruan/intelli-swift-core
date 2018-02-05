package com.fr.swift.segment.column.impl;

import com.fr.swift.cube.io.location.IResourceLocation;
import com.fr.swift.query.group.GroupType;
import com.fr.swift.segment.column.Column;
import com.fr.swift.segment.column.DetailColumn;
import com.fr.swift.util.function.Function;

/**
 * 日期子列，用于分组的
 *
 * @param <Derive> 新值
 * @param <Base>   原值
 * @author anchore
 * @date 2017/12/4
 * @see GroupType
 */
abstract class BaseSubDateColumn<Derive, Base> extends BaseColumn<Derive> {
    private GroupType type;

    /**
     * 源列，子列的父列
     */
    private Column<Base> origin;

    BaseSubDateColumn(IResourceLocation location, GroupType type, Column<Base> origin) {
        super(location);
        this.type = type;
        this.origin = origin;
    }

    @Override
    public DetailColumn<Derive> getDetailColumn() throws UnsupportedOperationException {
        return new DetailColumn<Derive>() {
            private DetailColumn<Base> baseDetail = origin.getDetailColumn();
            private Function<Base, Derive> deriver = DateDerivers.newDeriver(type);

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
                throw new UnsupportedOperationException();
            }
        };
    }
}