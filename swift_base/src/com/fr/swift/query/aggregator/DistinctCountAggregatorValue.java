package com.fr.swift.query.aggregator;

import com.fr.swift.bitmap.impl.RoaringMutableBitMap;

/**
 * @author Xiaolei.liu
 */

public class DistinctCountAggregatorValue implements AggregatorValue<Double> {


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

    @Override
    public Double calculateValue() {
        return Double.valueOf(bitMap.getCardinality());
    }

    @Override
    public Object clone() {
        DistinctCountAggregatorValue value = new DistinctCountAggregatorValue();
        value.bitMap = (RoaringMutableBitMap) RoaringMutableBitMap.newInstance();
        value.bitMap.or(this.bitMap);
        return value;
    }
}
