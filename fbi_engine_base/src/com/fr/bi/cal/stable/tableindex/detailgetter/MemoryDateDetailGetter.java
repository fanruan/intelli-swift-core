package com.fr.bi.cal.stable.tableindex.detailgetter;

import com.fr.bi.stable.engine.index.getter.DetailGetter;

import java.util.Date;
import java.util.List;


/**
 * Created by 小灰灰 on 2016/1/14.
 */
public class MemoryDateDetailGetter implements DetailGetter<Date>{
    private List<Long> list;

    public MemoryDateDetailGetter(List<Long> list) {
        this.list = list;
    }

    @Override
    public Date getValueObject(int row) {
        Long t = list.get(row);
        if (t != null) {
            return new Date(t);
        }
        return null;
    }

    @Override
    public Object getValue(int row) {
        return list.get(row);
    }
}