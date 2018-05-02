package com.fr.swift.query.group.by2.node;

import com.fr.swift.query.aggregator.Aggregator;
import com.fr.swift.query.aggregator.AggregatorValue;
import com.fr.swift.query.group.by.GroupByEntry;
import com.fr.swift.query.group.info.MetricInfo;
import com.fr.swift.result.TopGroupNode;
import com.fr.swift.result.XLeftNode;
import com.fr.swift.segment.column.Column;
import com.fr.swift.structure.iterator.RowTraversal;
import com.fr.swift.structure.stack.LimitedStack;
import com.fr.swift.util.function.Function2;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Lyon on 2018/4/27.
 */
public class XLeftNodeRowMapper implements Function2<GroupByEntry, LimitedStack<XLeftNode>, XLeftNode[]> {

    private int targetLength;
    private List<Column> metrics;
    private List<Aggregator> aggregators;
    private Iterable<TopGroupNode[]> colItCreator;

    public XLeftNodeRowMapper(MetricInfo metricInfo, Iterable<TopGroupNode[]> colItCreator) {
        this.targetLength = metricInfo.getTargetLength();
        this.metrics = metricInfo.getMetrics();
        this.aggregators = metricInfo.getAggregators();
        this.colItCreator = colItCreator;
    }

    @Override
    public XLeftNode[] apply(GroupByEntry groupByEntry, LimitedStack<XLeftNode> groupNodeLimitedStack) {
        Iterator<TopGroupNode[]> colIt = colItCreator.iterator();
        List<AggregatorValue[]> valuesArrayList = new ArrayList<AggregatorValue[]>();
        RowTraversal rowTraversal = groupByEntry.getTraversal();
        while (colIt.hasNext()) {
            TopGroupNode[] colRow = colIt.next();
            RowTraversal colTraversal = getColRowTraversal(colRow);
            RowTraversal metricIndex = rowTraversal.toBitMap().getAnd(colTraversal.toBitMap());
            valuesArrayList.add(GroupNodeRowMapper.aggregateRow(metricIndex, targetLength, metrics, aggregators));
        }
        groupNodeLimitedStack.peek().setValueArrayList(valuesArrayList);
        return groupNodeLimitedStack.toList().toArray(new XLeftNode[groupNodeLimitedStack.limit()]);
    }

    private static RowTraversal getColRowTraversal(TopGroupNode[] colRow) {
        for (int i = colRow.length - 1; i > -1; i--) {
            if (colRow[i] != null) {
                return colRow[i].getTraversal();
            }
        }
        return null;
    }
}
