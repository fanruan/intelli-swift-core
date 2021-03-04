// TODO: 2019/6/27 anchore 作为新类test的参考，先不删
//
// package com.fr.swift.result;
//
//import AscSort;
//import Sort;
//import ColumnTypeConstants;
//import ListBasedRow;
//import Row;
//import Pair;
//import junit.framework.TestCase;
//
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.Collections;
//import java.util.List;
//
///**
// * Created by lyon on 2018/12/30.
// */
//public class SortedDetailQueryResultSetMergerTest extends TestCase {
//
//    public void testMerge() {
//        int fetchSize = 5;
//        int numberOfPage = 3;
//        int pageSize = 10;
//        int value = 0;
//        List<List<Row>> pages = new ArrayList<List<Row>>();
//        for (int i = 0; i < numberOfPage; i++) {
//            List<Row> page = new ArrayList<Row>();
//            for (int j = 0; j < pageSize; j++) {
//                List row = Collections.singletonList(value++);
//                page.add(new ListBasedRow(row));
//            }
//            pages.add(page);
//        }
//        int rowCount = numberOfPage * pageSize;
//        int numberOfQRS = 3;
//        List<DetailQueryResultSet> resultSets = new ArrayList<DetailQueryResultSet>();
//        for (int i = 0; i < numberOfQRS; i++) {
//            resultSets.add(new DetailQueryResultSetMergerTest.TestDetailQRS(pageSize, rowCount, pages));
//        }
//        SortedDetailQueryResultSetMerger merger = SortedDetailQueryResultSetMerger.ofCompareInfo(fetchSize,
//                Arrays.asList(Pair.of((Sort) new AscSort(0), ColumnTypeConstants.ClassType.INTEGER)));
//        DetailQueryResultSet resultSet = merger.merge(resultSets);
//        assertEquals(numberOfQRS * rowCount, resultSet.getRowCount());
//        List<Row> rows = new ArrayList<Row>();
//        while (resultSet.hasNextPage()) {
//            rows.addAll(resultSet.getPage());
//        }
//        int totalRowCount = numberOfQRS * rowCount;
//        assertEquals(totalRowCount, rows.size());
//        for (int i = 1; i < rows.size(); i++) {
//            Row pre = rows.get(i - 1);
//            Row current = rows.get(i);
//            assertTrue((Integer) pre.getValue(0) <= (Integer) current.getValue(0));
//        }
//    }
//}