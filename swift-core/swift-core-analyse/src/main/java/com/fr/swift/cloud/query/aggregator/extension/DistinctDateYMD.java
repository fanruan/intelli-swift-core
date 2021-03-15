package com.fr.swift.cloud.query.aggregator.extension;

import com.fr.swift.cloud.bitmap.traversal.TraversalAction;
import com.fr.swift.cloud.query.aggregator.Aggregator;
import com.fr.swift.cloud.query.aggregator.AggregatorType;
import com.fr.swift.cloud.query.aggregator.AggregatorValue;
import com.fr.swift.cloud.query.aggregator.HLLAggregatorValue;
import com.fr.swift.cloud.query.aggregator.SingleColumnAggregator;
import com.fr.swift.cloud.query.group.GroupType;
import com.fr.swift.cloud.segment.column.Column;
import com.fr.swift.cloud.segment.column.DetailColumn;
import com.fr.swift.cloud.segment.column.impl.DateDerivers;
import com.fr.swift.cloud.structure.iterator.RowTraversal;
import com.fr.swift.cloud.util.function.Function;

import java.io.Serializable;

/**
 * @author lyon
 * @date 2018/10/24
 */
public class DistinctDateYMD extends SingleColumnAggregator<HLLAggregatorValue> implements Serializable {

    public static final Aggregator INSTANCE = new DistinctDateYMD();
    private static final long serialVersionUID = 2185374277400248976L;

    private static Function<Long, Long> fn = DateDerivers.newDeriver(GroupType.Y_M_D);

    @Override
    public HLLAggregatorValue aggregate(RowTraversal traversal, Column<?> column) {
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
    public HLLAggregatorValue createAggregatorValue(AggregatorValue<?> value) {
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
