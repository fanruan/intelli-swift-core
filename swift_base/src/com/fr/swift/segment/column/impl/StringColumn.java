package com.fr.swift.segment.column.impl;

import com.fr.swift.compare.Comparators;
import com.fr.swift.cube.io.location.IResourceLocation;
import com.fr.swift.segment.column.DetailColumn;
import com.fr.swift.segment.column.DictionaryEncodedColumn;
import com.fr.swift.segment.column.impl.base.FakeStringDetailColumn;
import com.fr.swift.segment.column.impl.base.StringDictColumn;

/**
 * @author anchore
 * @date 2017/11/30
 */
public class StringColumn extends BaseColumn<String> {
    private DetailColumn<String> detailColumn;

    public StringColumn(IResourceLocation location) {
        super(location);
    }

    @Override
    public DictionaryEncodedColumn<String> getDictionaryEncodedColumn() {
        return dictColumn != null ? dictColumn : (dictColumn = new StringDictColumn(location, Comparators.PINYIN_ASC));
    }

    /**
     * 没有事实上的detail，但需要从dict中计算出来，对外表现为实现了本接口
     */
    @Override
    public DetailColumn<String> getDetailColumn() {
        return detailColumn != null ? detailColumn : (detailColumn = new FakeStringDetailColumn(this));
    }
}