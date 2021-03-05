package com.fr.swift.cloud.bitmap.impl;

import com.fr.swift.cloud.bitmap.MutableBitMap;

/**
 * @author anchore
 */
public class BitSetMutableTest extends RoaringMutableTest {
    @Override
    MutableBitMap getMutableBitMap() {
        return BitSetMutableBitMap.newInstance();
    }
}
