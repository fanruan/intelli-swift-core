package com.fr.swift.segment.column.impl.multi;

import com.fr.swift.cube.io.location.IResourceLocation;
import com.fr.swift.segment.column.BitmapIndexedColumn;
import com.fr.swift.segment.column.Column;
import com.fr.swift.segment.column.DetailColumn;
import com.fr.swift.segment.column.DictionaryEncodedColumn;
import com.fr.swift.segment.column.impl.BaseColumn;

import java.util.ArrayList;
import java.util.List;

/**
 * @author anchore
 * @date 2019/7/11
 */
public class ReadonlyMultiColumn<T> extends BaseColumn<T> {
    private List<DetailColumn<T>> details = new ArrayList<DetailColumn<T>>();
    private List<DictionaryEncodedColumn<T>> dicts = new ArrayList<DictionaryEncodedColumn<T>>();
    private List<BitmapIndexedColumn> indices = new ArrayList<BitmapIndexedColumn>();

    private int[] offsets;

    public ReadonlyMultiColumn(List<Column<T>> columns, int[] offsets) {
        super(null);
        this.offsets = offsets;
        init(columns);
    }

    private void init(List<Column<T>> columns) {
        for (Column<T> column : columns) {
            details.add(column.getDetailColumn());
            dicts.add(column.getDictionaryEncodedColumn());
            indices.add(column.getBitmapIndex());
        }
    }

    @Override
    public DetailColumn<T> getDetailColumn() {
        return detailColumn == null ?
                detailColumn = new ReadonlyMultiDetailColumn<T>(details, offsets) :
                detailColumn;
    }

    @Override
    public DictionaryEncodedColumn<T> getDictionaryEncodedColumn() {
        return dictColumn == null ?
                dictColumn = new ReadonlyMultiDictColumn<T>(dicts, offsets) :
                dictColumn;
    }

    @Override
    public BitmapIndexedColumn getBitmapIndex() {
        return indexColumn == null ?
                indexColumn = new ReadonlyMultiBitmapColumn(indices, offsets, (ReadonlyMultiDictColumn<T>) getDictionaryEncodedColumn()) :
                indexColumn;
    }

    @Override
    public IResourceLocation getLocation() {
        throw new UnsupportedOperationException();
    }
}