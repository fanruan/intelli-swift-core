package com.fr.bi.stable.structure.collection.list;

import com.fr.bi.stable.utils.algorithem.BIRandomUitils;

/**
 * Created by Connery on 2015/12/3.
 */
public class BIIntegerListTest extends BIArrayListTest<Integer> {
    @Override
    protected BIArrayList<Integer> getList() {
        return new IntList();
    }

    @Override
    Integer getRandomValue() {
        return BIRandomUitils.getRandomInteger();
    }

}