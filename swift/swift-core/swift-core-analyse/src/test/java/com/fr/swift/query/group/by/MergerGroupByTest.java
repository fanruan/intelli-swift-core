//package com.fr.swift.query.group.by;
//
//import com.fr.swift.bitmap.BitMaps;
//import com.fr.swift.bitmap.ImmutableBitMap;
//import com.fr.swift.query.filter.detail.DetailFilter;
//import com.fr.swift.query.filter.match.MatchConverter;
//import com.fr.swift.query.group.info.IndexInfo;
//import com.fr.swift.result.SwiftNode;
//import com.fr.swift.segment.column.Column;
//import com.fr.swift.structure.Pair;
//import junit.framework.TestCase;
//
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.Comparator;
//import java.util.List;
//
///**
// * Created by pony on 2018/3/30.
// */
//public class MergerGroupByTest extends TestCase {
//
//    private MergerGroupBy mergerGroupByIndex;
//    private MergerGroupBy mergerGroupByValues;
//
//    public void setUp() {
//        CubeData cubeData1 = new CubeData();
//        CubeData cubeData2 = new CubeData();
//        List<Column> dimensions1 = new ArrayList<Column>();
//        for (Pair<Column, IndexInfo> pair : cubeData1.getDimensions()) {
//            dimensions1.add(pair.getKey());
//        }
//        List<Column> dimensions2 = new ArrayList<Column>();
//        for (Pair<Column, IndexInfo> pair : cubeData2.getDimensions()) {
//            dimensions2.add(pair.getKey());
//        }
//        int[] cursor = new int[dimensions1.size()];
//        boolean[] asc = new boolean[dimensions1.size()];
//        Arrays.fill(asc, true);
//        final int rowCount1 = cubeData1.getRowCount();
//        MultiGroupByIndex multiGroupByIndex1 = new MultiGroupByIndex(dimensions1, new DetailFilter() {
//            @Override
//            public ImmutableBitMap createFilterIndex() {
//                return BitMaps.newAllShowBitMap(rowCount1);
//            }
//
//            @Override
//            public boolean matches(SwiftNode node, int targetIndex, MatchConverter converter) {
//                return false;
//            }
//        }, cursor, asc, true);
//        MultiGroupByValues multiGroupByValues1 = new MultiGroupByValues(dimensions1, new DetailFilter() {
//            @Override
//            public ImmutableBitMap createFilterIndex() {
//                return BitMaps.newAllShowBitMap(rowCount1);
//            }
//
//            @Override
//            public boolean matches(SwiftNode node, int targetIndex, MatchConverter converter) {
//                return false;
//            }
//        }, cursor, asc);
//        final int rowCount2 = cubeData2.getRowCount();
//        MultiGroupByIndex multiGroupByIndex2 = new MultiGroupByIndex(dimensions2, new DetailFilter() {
//            @Override
//            public ImmutableBitMap createFilterIndex() {
//                return BitMaps.newAllShowBitMap(rowCount2);
//            }
//
//            @Override
//            public boolean matches(SwiftNode node, int targetIndex, MatchConverter converter) {
//                return false;
//            }
//        }, cursor, asc, true);
//        MultiGroupByValues multiGroupByValues2 = new MultiGroupByValues(dimensions2, new DetailFilter() {
//            @Override
//            public ImmutableBitMap createFilterIndex() {
//                return BitMaps.newAllShowBitMap(rowCount2);
//            }
//
//            @Override
//            public boolean matches(SwiftNode node, int targetIndex, MatchConverter converter) {
//                return false;
//            }
//        }, cursor, asc);
//        mergerGroupByIndex = new MergerGroupByIndex(new MultiGroupByIndex[]{multiGroupByIndex1, multiGroupByIndex2}, asc);
//        Comparator[] comparators = new Comparator[dimensions1.size()];
//        for (int i = 0; i < comparators.length; i++) {
//            comparators[i] = dimensions1.get(i).getDictionaryEncodedColumn().getComparator();
//        }
//        mergerGroupByValues = new MergerGroupByValues(new MultiGroupByValues[]{multiGroupByValues1, multiGroupByValues2}, comparators, asc);
//    }
//
//    public void testHasNext() {
//        assertEquals(mergerGroupByIndex.hasNext(), true);
//        assertEquals(mergerGroupByValues.hasNext(), true);
//    }
//
//    public void testNext() {
//        mergerGroupByIndex.hasNext();
//        mergerGroupByValues.hasNext();
//        assertNotNull(mergerGroupByIndex.next());
//        assertNotNull(mergerGroupByValues.hasNext());
//    }
//}