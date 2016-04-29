package com.fr.bi.stable.structure.collection.list;

import com.fr.bi.stable.utils.algorithem.BIRandomUitils;

/**
 * Created by Connery on 2015/12/3.
 */
public class BIByteListTest extends BIArrayListTest<Byte> {
    @Override
    protected BIArrayList<Byte> getList() {
        return new ByteList();
    }

    @Override
    Byte getRandomValue() {
        return BIRandomUitils.getRandomByte(1)[0];
    }

}