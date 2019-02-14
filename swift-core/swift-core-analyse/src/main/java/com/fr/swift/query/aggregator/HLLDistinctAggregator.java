package com.fr.swift.query.aggregator;

import com.fr.swift.bitmap.traversal.TraversalAction;
import com.fr.swift.segment.column.Column;
import com.fr.swift.segment.column.DetailColumn;
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
    private static final long serialVersionUID = 4052363273023659470L;

    private static HLLAggregatorValue aggString(RowTraversal traversal, final DetailColumn detailColumn) {
        final HLLAggregatorValue aggregatorValue = new HLLAggregatorValue();
        traversal.traversal(new TraversalAction() {
            @Override
            public void actionPerformed(int row) {
                aggregatorValue.offer((String) detailColumn.get(row));
            }
        });
        return aggregatorValue;
    }

    private static HLLAggregatorValue aggDouble(RowTraversal traversal, final DetailColumn detailColumn) {
        final HLLAggregatorValue aggregatorValue = new HLLAggregatorValue();
        traversal.traversal(new TraversalAction() {
            @Override
            public void actionPerformed(int row) {
                aggregatorValue.offer(detailColumn.getDouble(row));
            }
        });
        return aggregatorValue;
    }

    private static HLLAggregatorValue aggLong(RowTraversal traversal, final DetailColumn detailColumn) {
        final HLLAggregatorValue aggregatorValue = new HLLAggregatorValue();
        traversal.traversal(new TraversalAction() {
            @Override
            public void actionPerformed(int row) {
                aggregatorValue.offer(detailColumn.getLong(row));
            }
        });
        return aggregatorValue;
    }

    private static HLLAggregatorValue aggInt(RowTraversal traversal, final DetailColumn detailColumn) {
        final HLLAggregatorValue aggregatorValue = new HLLAggregatorValue();
        traversal.traversal(new TraversalAction() {
            @Override
            public void actionPerformed(int row) {
                aggregatorValue.offer(detailColumn.getInt(row));
            }
        });
        return aggregatorValue;
    }

    @Override
    public HLLAggregatorValue aggregate(RowTraversal traversal, Column column) {
        final DictionaryEncodedColumn dictionaryEncodedColumn = column.getDictionaryEncodedColumn();
        final ColumnTypeConstants.ClassType type = dictionaryEncodedColumn.getType();
        switch (type) {
            case INTEGER:
                return aggInt(traversal, column.getDetailColumn());
            case LONG:
                return aggLong(traversal, column.getDetailColumn());
            case DOUBLE:
                return aggDouble(traversal, column.getDetailColumn());
            default:
                return aggString(traversal, column.getDetailColumn());
        }
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
