package com.fr.bi.cal.stable.tableindex.detailgetter;

import com.finebi.cube.api.ICubeColumnDetailGetter;
import com.finebi.cube.api.PrimitiveDetailGetter;
import com.finebi.cube.api.PrimitiveType;
import com.fr.bi.stable.io.newio.NIOReader;

public class NormalDetailGetter implements ICubeColumnDetailGetter {

    private NIOReader reader;


    public NormalDetailGetter(NIOReader reader) {
        this.reader = reader;
    }




    @Override
    public Object getValue(int row) {
        return reader.get(row);
    }

    @Override
    public PrimitiveType getPrimitiveType() {
        return null;
    }

    @Override
    public PrimitiveDetailGetter createPrimitiveDetailGetter() {
        return null;
    }


}