package com.fr.bi.stable.structure.collection.list;

import com.fr.bi.stable.utils.algorithem.BIRandomUitils;

/**
 * Created by Connery on 2015/12/3.
 */
public class BIDoubleListTest extends BIArrayListTest<Double> {
    @Override
    protected BIArrayList<Double> getList() {
        return new DoubleList();
    }

    @Override
    Double getRandomValue() {
        return BIRandomUitils.getRandomDouble();
    }

}