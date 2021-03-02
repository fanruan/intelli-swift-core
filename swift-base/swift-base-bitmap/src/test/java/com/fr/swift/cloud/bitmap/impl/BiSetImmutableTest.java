package com.fr.swift.cloud.bitmap.impl;

import com.fr.swift.cloud.bitmap.MutableBitMap;

/**
 * @author anchore
 */
public class BiSetImmutableTest extends RoaringImmutableTest {
    @Override
    MutableBitMap getMutableBitMap() {
        return BitSetMutableBitMap.newInstance();
    }
}
