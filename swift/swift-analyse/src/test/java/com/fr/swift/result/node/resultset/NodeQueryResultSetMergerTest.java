package com.fr.swift.result.node.resultset;

import com.fr.swift.query.aggregator.AggregatorFactory;
import com.fr.swift.query.aggregator.AggregatorType;
import com.fr.swift.query.aggregator.AggregatorValue;
import com.fr.swift.query.aggregator.DoubleAmountAggregatorValue;
import com.fr.swift.query.sort.SortType;
import com.fr.swift.result.BaseNodeMergeQRS;
import com.fr.swift.result.GroupNode;
import com.fr.swift.result.NodeMergeQRS;
import com.fr.swift.result.SwiftNodeUtils;
import com.fr.swift.source.ColumnTypeConstants;
import com.fr.swift.source.Row;
import com.fr.swift.structure.Pair;
import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by lyon on 2018/12/30.
 */
public class NodeQueryResultSetMergerTest extends TestCase {

    public void testMerge() {
        int fetchSize = 3;
        int numberOfPages = 3;
        int pageSize = 6;
        List<Map<Integer, Object>> map = new ArrayList<Map<Integer, Object>>();
        double value = 5;
        int numberOfNodeQRS = 3;
        List<NodeMergeQRS<GroupNode>> resultSets = new ArrayList<NodeMergeQRS<GroupNode>>();
        for (int n = 0; n < numberOfNodeQRS; n++) {
            String k = "";
            List<Pair<GroupNode, List<Map<Integer, Object>>>> pages = new ArrayList<Pair<GroupNode, List<Map<Integer, Object>>>>();
            for (int i = 0; i < numberOfPages; i++) {
                String[] keys = new String[pageSize];
                double[] values = new double[pageSize];
                for (int j = 0; j < pageSize; j++) {
                    k += "a";
                    keys[j] = k;
                    values[j] = value;
                }
                pages.add(Pair.of(createNode(keys, values), map));
            }
            resultSets.add(new TestNodeQRS(fetchSize, pages));
        }
        NodeQueryResultSetMerger merger = new NodeQueryResultSetMerger(fetchSize, new boolean[]{false},
                Arrays.asList(AggregatorFactory.createAggregator(AggregatorType.COUNT)),
                Arrays.asList(Pair.of(SortType.ASC, ColumnTypeConstants.ClassType.STRING)));
        NodeMergeQRS<GroupNode> resultSet = merger.merge(resultSets);
        List<Row> rows = new ArrayList<Row>();
        while (resultSet.hasNextPage()) {
            Pair<GroupNode, List<Map<Integer, Object>>> page = resultSet.getPage();
            Iterator<Row> iterator = SwiftNodeUtils.node2RowIterator(page.getKey());
            while (iterator.hasNext()) {
                rows.add(iterator.next());
            }
        }
        int totalRowCount = numberOfPages * pageSize;
        assertEquals(totalRowCount, rows.size());
        for (int i = 0; i < rows.size(); i++) {
            assertEquals(i + 1, rows.get(i).getValue(0).toString().length());
            assertEquals(value * numberOfNodeQRS, rows.get(i).getValue(1));
        }
    }

    private static GroupNode createNode(String[] keys, double[] values) {
        GroupNode root = new GroupNode(-1, null);
        for (int i = 0; i < keys.length; i++) {
            GroupNode child = new GroupNode(0, keys[i]);
            child.setAggregatorValue(new AggregatorValue[]{new DoubleAmountAggregatorValue(values[i])});
            root.addChild(child);
        }
        return root;
    }

    private static class TestNodeQRS extends BaseNodeMergeQRS<GroupNode> {

        Iterator<Pair<GroupNode, List<Map<Integer, Object>>>> iterator;

        public TestNodeQRS(int fetchSize, List<Pair<GroupNode, List<Map<Integer, Object>>>> pages) {
            super(fetchSize);
            this.iterator = pages.iterator();
        }

        @Override
        public Pair<GroupNode, List<Map<Integer, Object>>> getPage() {
            return iterator.next();
        }

        @Override
        public boolean hasNextPage() {
            return iterator.hasNext();
        }
    }
}