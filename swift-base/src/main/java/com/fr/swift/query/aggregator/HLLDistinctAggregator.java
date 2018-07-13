package com.fr.swift.query.aggregator;

import com.fr.swift.bitmap.traversal.CalculatorTraversalAction;
import com.fr.swift.segment.column.Column;
import com.fr.swift.segment.column.DictionaryEncodedColumn;
import com.fr.swift.source.ColumnTypeConstants;
import com.fr.swift.structure.iterator.RowTraversal;

/**
 * 这边用的是这个库https://github.com/addthis/stream-lib中提供的实现
 * 该库包含其他统计流数据的基于概率数据结构的算法实现，后面相关需求可以该库加进来
 * <p>
 * Created by Lyon on 2018/7/13.
 */
public class HLLDistinctAggregator implements Aggregator<HLLAggregatorValue> {

    protected static final Aggregator INSTANCE = new HLLDistinctAggregator();

    @Override
    public HLLAggregatorValue aggregate(RowTraversal traversal, Column column) {
        final HLLAggregatorValue aggregatorValue = new HLLAggregatorValue();
        final DictionaryEncodedColumn dictionaryEncodedColumn = column.getDictionaryEncodedColumn();
        final ColumnTypeConstants.ClassType type = dictionaryEncodedColumn.getType();
        traversal.traversal(new CalculatorTraversalAction() {
            @Override
            public double getCalculatorValue() {
                return 0;
            }

            @Override
            public void actionPerformed(int row) {
                switch (type) {
                    case INTEGER:
                        aggregatorValue.offer((Integer) dictionaryEncodedColumn.getValueByRow(row));
                        break;
                    case LONG:
                        aggregatorValue.offer((Long) dictionaryEncodedColumn.getValueByRow(row));
                        break;
                    case DOUBLE:
                        aggregatorValue.offer((Double) dictionaryEncodedColumn.getValueByRow(row));
                        break;
                    default:
                        aggregatorValue.offer((String) dictionaryEncodedColumn.getValueByRow(row));
                }
            }
        });
        return aggregatorValue;
    }

    @Override
    public HLLAggregatorValue createAggregatorValue(AggregatorValue value) {
        return new HLLAggregatorValue();
    }

    @Override
    public AggregatorType getAggregatorType() {
        return AggregatorType.HLL_DISTINCT;
    }

    @Override
    public void combine(HLLAggregatorValue current, HLLAggregatorValue other) {
        current.addAll(other);
    }
}
