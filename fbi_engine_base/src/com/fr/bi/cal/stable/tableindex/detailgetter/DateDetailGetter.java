package com.fr.bi.cal.stable.tableindex.detailgetter;

import com.fr.bi.stable.engine.index.getter.DetailGetter;
import com.fr.bi.stable.io.newio.NIOReader;

import java.util.Date;

public class DateDetailGetter implements DetailGetter<Date> {

    private DetailGetter<Long> getter;

    public DateDetailGetter(NIOReader<Long> reader) {
        getter = new NormalDetailGetter<Long>(reader);
    }

    public DateDetailGetter() {
    }

    @Override
    public Date getValueObject(int row) {
        Long t = getter.getValueObject(row);
        if (t != null) {
            return new Date(t);
        }
        return null;
    }

    @Override
    public Object getValue(int row) {
        return getter.getValueObject(row);
    }

}