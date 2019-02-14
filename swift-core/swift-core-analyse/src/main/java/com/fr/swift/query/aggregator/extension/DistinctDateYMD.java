package com.fr.swift.query.aggregator.extension;

import com.fr.swift.bitmap.traversal.TraversalAction;
import com.fr.swift.query.aggregator.Aggregator;
import com.fr.swift.query.aggregator.AggregatorType;
import com.fr.swift.query.aggregator.AggregatorValue;
import com.fr.swift.query.aggregator.HLLAggregatorValue;
import com.fr.swift.query.group.GroupType;
import com.fr.swift.segment.column.Column;
import com.fr.swift.segment.column.DetailColumn;
import com.fr.swift.segment.column.impl.DateDerivers;
import com.fr.swift.structure.iterator.RowTraversal;
import com.fr.swift.util.function.Function;

/**
 * Created by lyon on 2018/10/24.
 */
public class DistinctDateYMD implements Aggregator<HLLAggregatorValue> {

    public static final Aggregator INSTANCE = new DistinctDateYMD();
    private static final long serialVersionUID = 2185374277400248976L;

    private static Function<Long, Long> fn = DateDerivers.newDeriver(GroupType.Y_M_D);

    @Override
    public HLLAggregatorValue aggregate(RowTraversal traversal, Column column) {
        final DetailColumn detailColumn = column.getDetailColumn();
        final HLLAggregatorValue aggregatorValue = new HLLAggregatorValue();
        traversal.traversal(new TraversalAction() {
            @Override
            public void actionPerformed(int row) {
                aggregatorValue.offer(fn.apply(detailColumn.getLong(row)));
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
        return AggregatorType.DISTINCT_DATE_YMD;
    }

    @Override
    public void combine(HLLAggregatorValue current, HLLAggregatorValue other) {
        current.addAll(other);
    }
}
