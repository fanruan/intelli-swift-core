package com.fr.bi.cal.stable.tableindex.detailgetter;

import com.fr.bi.stable.engine.index.getter.DetailGetter;

/**
 * Created by 小灰灰 on 2016/1/14.
 */
public class MemoryDateDetailGetter extends DateDetailGetter{
    private DetailGetter<Long> getter;

    public MemoryDateDetailGetter(DetailGetter<Long> getter) {
        this.getter = getter;
    }
}