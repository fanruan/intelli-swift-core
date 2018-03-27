package com.fr.swift.query.aggregator;

import com.fr.swift.bitmap.traversal.TraversalAction;
import com.fr.swift.segment.column.Column;
import com.fr.swift.segment.column.DictionaryEncodedColumn;
import com.fr.swift.structure.iterator.RowTraversal;

/**
 * Created by pony on 2018/3/26.
 */
public class StringCombineAggregate implements Aggregator<StringAggregateValue> {
    protected static final Aggregator INSTANCE = new StringCombineAggregate();
    private static final char TAG = ',';

    @Override
    public StringAggregateValue aggregate(RowTraversal traversal, final Column column) {
        StringAggregateValue stringAggregateValue = new StringAggregateValue();
        final StringBuffer sb = new StringBuffer();
        final DictionaryEncodedColumn dic = column.getDictionaryEncodedColumn();
        traversal.traversal(new TraversalAction() {
            @Override
            public void actionPerformed(int row) {
                Object v = dic.getValue(dic.getIndexByRow(row));
                if (v != null){
                    sb.append(v).append(TAG);
                }
            }
        });
        sb.deleteCharAt(sb.length() - 1);
        stringAggregateValue.setValue(sb.toString());
        return stringAggregateValue;
    }

    @Override
    public void combine(StringAggregateValue value, StringAggregateValue other) {
        value.setValue(value.getValue() + TAG + other.getValue());
    }
}
