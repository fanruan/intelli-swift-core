package com.fr.bi.cal.stable.tableindex.detailgetter;

import com.finebi.cube.api.ICubeColumnDetailGetter;
import com.fr.bi.cal.stable.cube.memory.AnyIndexArray;

/**
 * Created by 小灰灰 on 2016/1/14.
 */
public class MemoryDetailGetter implements ICubeColumnDetailGetter {
    AnyIndexArray list;

    public MemoryDetailGetter(AnyIndexArray list) {
        this.list = list;
    }

    @Override
    public Object getValue(int row) {
        return list.get(row);
    }
}