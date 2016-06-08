package com.fr.bi.cal.stable.cube.memory;

import com.fr.bi.base.key.BIKey;
import com.fr.bi.cal.stable.tableindex.detailgetter.MemoryDetailGetter;
import com.fr.bi.stable.engine.index.getter.DetailGetter;
import com.fr.bi.stable.io.newio.SingleUserNIOReadManager;

/**
 * Created by 小灰灰 on 2016/1/14.
 */
public class MemoryIntegerColumn extends AbstractSingleMemoryColumn<Integer> {
    @Override
    public DetailGetter<Integer> createDetailGetter(SingleUserNIOReadManager manager) {
        return new MemoryDetailGetter<Integer>(detail);
    }


    @Override
    protected void initDetail() {
        detail = new AnyIndexArray<Integer>(new NullChecker<Integer>() {
            @Override
            public boolean isNull(Integer v) {
                return v ==null || v == Integer.MAX_VALUE;
            }
        });
    }

    @Override
    protected Object createEmptyValue(BIKey key) {
        return Integer.MAX_VALUE;
    }
}