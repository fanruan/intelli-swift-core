package com.fr.swift.cloud.query.aggregator.extension;

import com.fr.swift.cloud.bitmap.traversal.BreakTraversalAction;
import com.fr.swift.cloud.query.aggregator.AggregatorType;
import com.fr.swift.cloud.query.aggregator.AggregatorValue;
import com.fr.swift.cloud.query.aggregator.SingleColumnAggregator;
import com.fr.swift.cloud.segment.column.Column;
import com.fr.swift.cloud.structure.iterator.RowTraversal;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Moira
 * @date 2020/2/3
 * @description 分组聚合后每组只保留前几行的数据
 * @since swift 1.0
 */
public class LimitRowAggregator extends SingleColumnAggregator<LimitRowAggregatorValue> implements Serializable {

    private static final long serialVersionUID = -871211408339179720L;

    private int limitRow;

    public LimitRowAggregator(int limitRow) {
        this.limitRow = limitRow;
    }

    @Override
    public LimitRowAggregatorValue aggregate(RowTraversal traversal, final Column column) {
        LimitRowAggregatorValue value = new LimitRowAggregatorValue();
        final List<Object> list = new ArrayList<>();
        traversal.breakableTraversal(new BreakTraversalAction() {
            @Override
            public boolean actionPerformed(int row) {
                list.add(column.getDictionaryEncodedColumn().getValueByRow(row));
                return limitRow <= list.size();
            }
        });
        value.setValue(list);
        return value;
    }

    @Override
    public LimitRowAggregatorValue createAggregatorValue(AggregatorValue<?> value) {
        return new LimitRowAggregatorValue();
    }

    @Override
    public AggregatorType getAggregatorType() {
        return AggregatorType.LIMIT_ROW;
    }

    @Override
    public void combine(LimitRowAggregatorValue value, LimitRowAggregatorValue other) {
        value.calculateValue().addAll(other.calculateValue());
    }
}
