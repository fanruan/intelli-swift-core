package com.fr.bi.cal.stable.io;

import com.fr.bi.stable.gvi.array.ICubeTableIndexReader;
import com.fr.bi.stable.io.sortlist.ISortNIOReadList;

public class NIOReadGroupMap<T> extends AbstractReadGroupMap<T> {

    public NIOReadGroupMap() {
        super();
    }

    public NIOReadGroupMap(ISortNIOReadList<T> lmp, ICubeTableIndexReader indexes) {
        super(lmp, indexes);
    }

}