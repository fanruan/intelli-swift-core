package com.fr.swift.query.aggregator.extension;

import com.fr.swift.bitmap.traversal.BreakTraversalAction;
import com.fr.swift.query.aggregator.AggregatorType;
import com.fr.swift.query.aggregator.AggregatorValue;
import com.fr.swift.query.aggregator.SingleColumnAggregator;
import com.fr.swift.segment.column.Column;
import com.fr.swift.structure.iterator.RowTraversal;

import java.io.Serializable;

/**
 * @author Moira
 * @date 2020/2/3
 * @description 分组聚合后每组只保留第一行的数据
 * @since swift 1.0
 */
public class FirstRowAggregator extends SingleColumnAggregator<FirstRowAggregatorValue> implements Serializable {

    public static final FirstRowAggregator INSTANCE = new FirstRowAggregator();
    private static final long serialVersionUID = -871211408339179720L;

    @Override
    public FirstRowAggregatorValue aggregate(RowTraversal traversal, final Column column) {
        FirstRowAggregatorValue value = new FirstRowAggregatorValue();
        final Object[] a = new Object[1];
        traversal.breakableTraversal(new BreakTraversalAction() {
            @Override
            public boolean actionPerformed(int row) {
                a[0] = column.getDictionaryEncodedColumn().getValueByRow(row);
                return true;
            }
        });
        value.setValue(a[0]);
        return value;
    }

    @Override
    public FirstRowAggregatorValue createAggregatorValue(AggregatorValue<?> value) {
        return new FirstRowAggregatorValue();
    }

    @Override
    public AggregatorType getAggregatorType() {
        return AggregatorType.FIRST_ROW;
    }

    @Override
    public void combine(FirstRowAggregatorValue current, FirstRowAggregatorValue other) {
    }
}
