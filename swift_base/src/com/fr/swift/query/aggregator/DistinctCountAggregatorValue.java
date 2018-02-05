package com.fr.swift.query.aggregator;

import com.fr.swift.bitmap.impl.BitSetMutableBitMap;
import com.fr.swift.bitmap.impl.RoaringMutableBitMap;

/**
 * @author Xiaolei.liu
 */

public class DistinctCountAggregatorValue implements AggregatorValue<BitSetMutableBitMap> {


    private RoaringMutableBitMap bitMap;


    public void setBitMap(RoaringMutableBitMap bitMap) {
        this.bitMap = bitMap;
    }


    public RoaringMutableBitMap getBitMap() {
        return bitMap;
    }

    @Override
    public double calculate() {
        return bitMap.getCardinality();
    }
}
