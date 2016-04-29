package com.fr.bi.cal.stable.tableindex.detailgetter;

import com.fr.bi.stable.io.newio.NIOReader;

public class DoubleDetailGetter extends NormalDetailGetter<Double> {

    public DoubleDetailGetter(NIOReader<Double> reader) {
        super(reader);
    }


    @Override
    public Double getValueObject(int row) {
        Double d = super.getValueObject(row);
        return Double.isNaN(d) ? null : d;
    }


}