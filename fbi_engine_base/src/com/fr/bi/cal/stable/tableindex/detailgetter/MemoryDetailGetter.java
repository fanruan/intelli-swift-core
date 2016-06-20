package com.fr.bi.cal.stable.tableindex.detailgetter;

import com.fr.bi.cal.stable.cube.memory.AnyIndexArray;
import com.fr.bi.stable.engine.index.getter.DetailGetter;

/**
 * Created by 小灰灰 on 2016/1/14.
 */
public class MemoryDetailGetter<T> implements DetailGetter<T> {
    AnyIndexArray<T> list;

    public MemoryDetailGetter(AnyIndexArray<T> list) {
        this.list = list;
    }

    @Override
    public T getValueObject(int row) {
        return list.get(row);
    }

    @Override
    public Object getValue(int row) {
        return getValueObject(row);
    }
}