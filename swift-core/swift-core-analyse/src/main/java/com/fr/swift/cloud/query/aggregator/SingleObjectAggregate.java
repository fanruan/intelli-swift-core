package com.fr.swift.cloud.query.aggregator;

import com.fr.swift.cloud.bitmap.traversal.BreakTraversalAction;
import com.fr.swift.cloud.segment.column.Column;
import com.fr.swift.cloud.segment.column.DictionaryEncodedColumn;
import com.fr.swift.cloud.structure.iterator.RowTraversal;

import java.io.Serializable;

/**
 * @author yee
 * @date 2019/10/30
 */
public class SingleObjectAggregate extends SingleColumnAggregator<ObjectAggregateValue> implements Serializable {
    protected static final SingleObjectAggregate INSTANCE = new SingleObjectAggregate();
    private static final long serialVersionUID = -8127435491427004323L;

    @Override
    public ObjectAggregateValue aggregate(RowTraversal traversal, final Column column) {
        final ObjectAggregateValue stringAggregateValue = new ObjectAggregateValue();
        final DictionaryEncodedColumn dic = column.getDictionaryEncodedColumn();
        traversal.breakableTraversal(new BreakTraversalAction() {
            @Override
            public boolean actionPerformed(int row) {
                final Object valueByRow = dic.getValueByRow(row);
                stringAggregateValue.setValue(valueByRow);
                return true;
            }
        });

        return stringAggregateValue;
    }

    @Override
    public ObjectAggregateValue createAggregatorValue(AggregatorValue<?> value) {
        return new ObjectAggregateValue();
    }

    @Override
    public AggregatorType getAggregatorType() {
        return AggregatorType.SINGLE_VALUE;
    }

    @Override
    public void combine(ObjectAggregateValue value, ObjectAggregateValue other) {
    }
}
