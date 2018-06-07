package com.fr.swift.segment.column.impl;

import com.fr.swift.cube.io.location.IResourceLocation;
import com.fr.swift.segment.column.BitmapIndexedColumn;
import com.fr.swift.segment.column.Column;
import com.fr.swift.segment.column.DetailColumn;
import com.fr.swift.segment.column.DictionaryEncodedColumn;
import com.fr.swift.segment.column.impl.base.BitMapColumn;

/**
 * @author anchore
 * @date 2017/11/30
 */
public abstract class BaseColumn<T> implements Column<T> {
    protected DetailColumn<T> detailColumn;

    protected DictionaryEncodedColumn<T> dictColumn;

    protected BitmapIndexedColumn indexColumn;

    protected IResourceLocation location;

    protected BaseColumn(IResourceLocation location) {
        this.location = location;
    }

    @Override
    public BitmapIndexedColumn getBitmapIndex() {
        return indexColumn != null ? indexColumn : (indexColumn = new BitMapColumn(location));
    }

    @Override
    public IResourceLocation getLocation() {
        return location;
    }
}