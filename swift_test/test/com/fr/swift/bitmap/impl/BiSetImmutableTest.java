package com.fr.swift.bitmap.impl;

import com.fr.swift.bitmap.MutableBitMap;

/**
 * @author anchore
 */
public class BiSetImmutableTest extends RoaringImmutableTest {
    @Override
    MutableBitMap getMutableBitMap() {
        return BitSetMutableBitMap.newInstance();
    }
}
