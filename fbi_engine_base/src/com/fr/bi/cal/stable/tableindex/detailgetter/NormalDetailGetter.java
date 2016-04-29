package com.fr.bi.cal.stable.tableindex.detailgetter;

import com.fr.bi.stable.engine.index.getter.DetailGetter;
import com.fr.bi.stable.io.newio.NIOReader;

public class NormalDetailGetter<T> implements DetailGetter<T> {

    private NIOReader<T> reader;


    public NormalDetailGetter(NIOReader<T> reader) {
        this.reader = reader;
    }


    @Override
    public T getValueObject(int row) {
        return reader.get(row);
    }


    @Override
    public Object getValue(int row) {
        return getValueObject(row);
    }


}