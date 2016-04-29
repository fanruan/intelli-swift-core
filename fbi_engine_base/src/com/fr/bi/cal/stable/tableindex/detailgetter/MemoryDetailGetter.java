package com.fr.bi.cal.stable.tableindex.detailgetter;

import com.fr.bi.stable.engine.index.getter.DetailGetter;

import java.util.List;

/**
 * Created by 小灰灰 on 2016/1/14.
 */
public class MemoryDetailGetter<T> implements DetailGetter<T> {
    List<T> list;

    public MemoryDetailGetter(List<T> list) {
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