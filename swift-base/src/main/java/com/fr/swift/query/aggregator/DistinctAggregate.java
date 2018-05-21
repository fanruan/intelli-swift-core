package com.fr.swift.query.aggregator;

import com.fr.swift.bitmap.MutableBitMap;
import com.fr.swift.bitmap.impl.RoaringMutableBitMap;
import com.fr.swift.bitmap.traversal.CalculatorTraversalAction;
import com.fr.swift.segment.column.Column;
import com.fr.swift.structure.iterator.RowTraversal;

/**
 * @author Xiaolei.liu
 */

public class DistinctAggregate implements Aggregator<DistinctCountAggregatorValue> {

    protected static final Aggregator INSTANCE = new DistinctAggregate();

    @Override
    public DistinctCountAggregatorValue aggregate(RowTraversal traversal, final Column column) {

        DistinctCountAggregatorValue distinctCount = new DistinctCountAggregatorValue();
        final MutableBitMap bitMap = RoaringMutableBitMap.newInstance();
        traversal.traversal(new CalculatorTraversalAction() {
            @Override
            public double getCalculatorValue() {
                return 0;
            }

            @Override
            public void actionPerformed(int row) {
                bitMap.add(column.getDictionaryEncodedColumn().getGlobalIndexByRow(row));
            }
        });
        distinctCount.setBitMap((RoaringMutableBitMap) bitMap);
        return distinctCount;
    }

    @Override
    public DistinctCountAggregatorValue createAggregatorValue(AggregatorValue value) {
        //去重计数只有明细合计
//        return new DistinctCountAggregatorValue();
        return (DistinctCountAggregatorValue) value;
    }

    @Override
    public void combine(DistinctCountAggregatorValue value, DistinctCountAggregatorValue other) {
        value.getBitMap().or(other.getBitMap());
    }
}
