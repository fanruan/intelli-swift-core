package com.fr.bi.stable.structure.collection.list;

import com.fr.bi.stable.utils.algorithem.BIRandomUitils;

/**
 * Created by Connery on 2015/12/3.
 */
public class BILongListTest extends BIArrayListTest<Long> {
    @Override
    protected BIArrayList<Long> getList() {
        return new LongList();
    }

    @Override
    Long getRandomValue() {
        return BIRandomUitils.getRandomLong();
    }


}