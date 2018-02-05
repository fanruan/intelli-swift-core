package com.fr.swift.bitmap.impl;

import com.fr.swift.bitmap.MutableBitMap;

/**
 * @author anchore
 */
public class BitSetMutableTest extends RoaringMutableTest {
    @Override
    MutableBitMap getMutableBitMap() {
        return BitSetMutableBitMap.newInstance();
    }
}
