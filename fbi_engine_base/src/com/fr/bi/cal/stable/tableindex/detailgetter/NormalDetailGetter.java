package com.fr.bi.cal.stable.tableindex.detailgetter;

import com.finebi.cube.api.ICubeColumnDetailGetter;
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


}