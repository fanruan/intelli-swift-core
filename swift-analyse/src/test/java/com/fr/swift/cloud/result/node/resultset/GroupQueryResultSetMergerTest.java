// TODO: 2019/6/27 anchore 作为新类test的参考，先不删
//
//package com.fr.swift.result.node.resultset;
//
//import AggregatorFactory;
//import AggregatorType;
//import AggregatorValue;
//import DoubleAmountAggregatorValue;
//import com.fr.swift.query.aggregator.ListAggregatorValueRow;
//import com.fr.swift.query.aggregator.SingleAggregatorValueSet;
//import GroupPage;
//import SortType;
//import com.fr.swift.result.BaseNodeMergeQRS;
//import GroupNode;
//import SwiftNodeUtils;
//import QueryResultSet;
//import ColumnTypeConstants.ClassType;
//import Row;
//import Pair;
//import junit.framework.TestCase;
//
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.Iterator;
//import java.util.List;
//import java.util.Map;
//
///**
// * Created by lyon on 2018/12/30.
// */
//public class GroupQueryResultSetMergerTest extends TestCase {
//
//    public void testMerge() {
//        int fetchSize = 3;
//        int numberOfPages = 3;
//        int pageSize = 6;
//        List<Map<Integer, Object>> map = new ArrayList<Map<Integer, Object>>();
//        double value = 5;
//        int numberOfNodeQRS = 3;
//        List<QueryResultSet<GroupPage>> resultSets = new ArrayList<QueryResultSet<GroupPage>>();
//        for (int n = 0; n < numberOfNodeQRS; n++) {
//            String k = "";
//            List<GroupPage> pages = new ArrayList<GroupPage>();
//            for (int i = 0; i < numberOfPages; i++) {
//                String[] keys = new String[pageSize];
//                double[] values = new double[pageSize];
//                for (int j = 0; j < pageSize; j++) {
//                    k += "a";
//                    keys[j] = k;
//                    values[j] = value;
//                }
//                pages.add(new GroupPage(createNode(keys, values), map));
//            }
//            resultSets.add(new TestNodeQRS(fetchSize, pages));
//        }
//        GroupQueryResultSetMerger merger = GroupQueryResultSetMerger.ofCompareInfo(fetchSize, new boolean[]{false},
//                Collections.singletonList(AggregatorFactory.createAggregator(AggregatorType.COUNT)),
//                Collections.singletonList(Pair.of(SortType.ASC, ClassType.STRING)));
//        QueryResultSet<GroupPage> resultSet = merger.merge(resultSets);
//        List<Row> rows = new ArrayList<Row>();
//        while (resultSet.hasNextPage()) {
//            GroupPage page = resultSet.getPage();
//            Iterator<Row> iterator = SwiftNodeUtils.node2RowIterator(page.getRoot());
//            while (iterator.hasNext()) {
//                rows.add(iterator.next());
//            }
//        }
//        int totalRowCount = numberOfPages * pageSize;
//        assertEquals(totalRowCount, rows.size());
//        for (int i = 0; i < rows.size(); i++) {
//            assertEquals(i + 1, rows.get(i).getValue(0).toString().length());
//            assertEquals(value * numberOfNodeQRS, rows.get(i).getValue(1));
//        }
//    }
//
//    private static GroupNode createNode(String[] keys, double[] values) {
//        GroupNode root = new GroupNode(-1, null);
//        for (int i = 0; i < keys.length; i++) {
//            GroupNode child = new GroupNode(0, keys[i]);
//            child.setAggregatorValue(new SingleAggregatorValueSet(new ListAggregatorValueRow(
//                    new AggregatorValue[]{new DoubleAmountAggregatorValue(values[i])})));
//            root.addChild(child);
//        }
//        return root;
//    }
//
//    private static class TestNodeQRS extends BaseNodeMergeQRS {
//
//        Iterator<GroupPage> iterator;
//
//        TestNodeQRS(int fetchSize, List<GroupPage> pages) {
//            super(fetchSize);
//            this.iterator = pages.iterator();
//        }
//
//        @Override
//        public GroupPage getPage() {
//            return iterator.next();
//        }
//
//        @Override
//        public boolean hasNextPage() {
//            return iterator.hasNext();
//        }
//    }
//}