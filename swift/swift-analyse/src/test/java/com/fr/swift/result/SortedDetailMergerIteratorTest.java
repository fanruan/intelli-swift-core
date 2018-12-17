//package com.fr.swift.result;
//
//import com.fr.swift.compare.Comparators;
//import com.fr.swift.source.ListBasedRow;
//import com.fr.swift.source.Row;
//import com.fr.swift.source.SwiftMetaData;
//import junit.framework.TestCase;
//
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.Comparator;
//import java.util.Iterator;
//import java.util.List;
//
///**
// * Created by Lyon on 2018/7/20.
// */
//public class SortedDetailMergerIteratorTest extends TestCase {
//
//    public void test() {
//        DetailResultSetImpl resultSet1 = new DetailResultSetImpl(new int[]{2, 3, 5, 5});
//        DetailResultSetImpl resultSet2 = new DetailResultSetImpl(new int[]{1, 2, 2, 3});
//        DetailResultSetImpl resultSet3 = new DetailResultSetImpl(new int[]{3, 5, 6, 6});
//        List<DetailResultSet> resultSets = Arrays.<DetailResultSet>asList(resultSet1, resultSet2, resultSet3);
//        Comparator<Row> comparator = new Comparator<Row>() {
//            Comparator<Integer> c = Comparators.asc();
//
//            @Override
//            public int compare(Row o1, Row o2) {
//                return c.compare(o1.<Integer>getValue(0), o2.<Integer>getValue(0));
//            }
//        };
//        Iterator<List<Row>> iterator = new SortedRowIterator(2, comparator, resultSets);
//        // [1, 2, 2, 2, 3, 3, 3, 5, 5, 5, 6, 6]
//        // 取每个resultSet进行合并得到[1, 2, 2, 3, 3, 6]，取出第一页[1, 2]
//        assertTrue(iterator.hasNext());
//        List<Row> rows = iterator.next();
//        assertEquals(1, rows.get(0).getValue(0));
//        assertEquals(2, rows.get(1).getValue(0));
//        assertEquals(resultSet1.cursor, 2);
//        assertEquals(resultSet2.cursor, 2);
//        assertEquals(resultSet3.cursor, 2);
//
//        // remainRows = [2, 3, 3, 5]，更新resultSet2得到[2, 2, 3, 3, 3, 5]，取出第二页[2, 2]
//        assertTrue(iterator.hasNext());
//        rows = iterator.next();
//        assertEquals(2, rows.get(0).getValue(0));
//        assertEquals(2, rows.get(1).getValue(0));
//        assertEquals(resultSet1.cursor, 2);
//        assertEquals(resultSet2.cursor, 4);
//        assertEquals(resultSet3.cursor, 2);
//
//        // remainRows = [3, 3, 3, 5]，无需更新resultSet，取出三页[3, 3]
//        assertTrue(iterator.hasNext());
//        rows = iterator.next();
//        assertEquals(3, rows.get(0).getValue(0));
//        assertEquals(3, rows.get(1).getValue(0));
//        assertEquals(resultSet1.cursor, 2);
//        assertEquals(resultSet2.cursor, 4);
//        assertEquals(resultSet3.cursor, 2);
//
//        // remainRows = [3, 5]，更新resultSet1得到[3, 5, 5, 5]，取出第四页[3, 5]
//        assertTrue(iterator.hasNext());
//        rows = iterator.next();
//        assertEquals(3, rows.get(0).getValue(0));
//        assertEquals(5, rows.get(1).getValue(0));
//        assertEquals(resultSet1.cursor, 4);
//        assertEquals(resultSet2.cursor, 4);
//        assertEquals(resultSet3.cursor, 2);
//
//        // remainRows = [5, 5]，无需更新resultSet，取出第五页[5, 5]
//        assertTrue(iterator.hasNext());
//        rows = iterator.next();
//        assertEquals(5, rows.get(0).getValue(0));
//        assertEquals(5, rows.get(1).getValue(0));
//        assertEquals(resultSet1.cursor, 4);
//        assertEquals(resultSet2.cursor, 4);
//        assertEquals(resultSet3.cursor, 2);
//
//        // remainRows = []，更新resultSet3得到[6, 6]，取出第六页[6, 6]
//        assertTrue(iterator.hasNext());
//        rows = iterator.next();
//        assertEquals(6, rows.get(0).getValue(0));
//        assertEquals(6, rows.get(1).getValue(0));
//        assertEquals(resultSet1.cursor, 4);
//        assertEquals(resultSet2.cursor, 4);
//        assertEquals(resultSet3.cursor, 4);
//
//        assertFalse(iterator.hasNext());
//    }
//
//    private static class DetailResultSetImpl implements DetailResultSet {
//
//        int pageSize = 2;
//        int cursor = 0;
//        List<Row> rows;
//
//        DetailResultSetImpl(int[] values) {
//            rows = new ArrayList<Row>();
//            for (int i = 0; i < values.length; i++) {
//                rows.add(new ListBasedRow(Arrays.asList(values[i])));
//            }
//        }
//
//        @Override
//        public int getFetchSize() {
//            return pageSize;
//        }
//
//        @Override
//        public List<Row> getPage() {
//            List<Row> result = new ArrayList<Row>();
//            for (int i = 0; i < pageSize; i++) {
//                if (cursor >= rows.size()) {
//                    break;
//                }
//                result.add(rows.get(cursor++));
//            }
//            return result;
//        }
//
//        @Override
//        public boolean hasNextPage() {
//            return cursor < rows.size();
//        }
//
//        @Override
//        public int getRowCount() {
//            return rows.size();
//        }
//
//        @Override
//        public void setMetaData(SwiftMetaData metaData) {
//        }
//
//        @Override
//        public SwiftMetaData getMetaData() {
//            return null;
//        }
//
//        @Override
//        public boolean hasNext() {
//            return false;
//        }
//
//        @Override
//        public Row getNextRow() {
//            return null;
//        }
//
//        @Override
//        public void close() {
//
//        }
//    }
//}
