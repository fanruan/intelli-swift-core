//package com.fr.swift.query.group.by;
//
//import com.fr.swift.bitmap.BitMaps;
//import com.fr.swift.bitmap.ImmutableBitMap;
//import com.fr.swift.bitmap.traversal.TraversalAction;
//import com.fr.swift.query.filter.detail.DetailFilter;
//import com.fr.swift.query.filter.match.MatchConverter;
//import com.fr.swift.query.group.info.IndexInfo;
//import com.fr.swift.result.KeyValue;
//import com.fr.swift.result.SwiftNode;
//import com.fr.swift.result.row.RowIndexKey;
//import com.fr.swift.segment.column.Column;
//import com.fr.swift.structure.Pair;
//import com.fr.swift.structure.iterator.RowTraversal;
//import junit.framework.TestCase;
//
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.Iterator;
//import java.util.List;
//import java.util.Map;
//
///**
// * Created by Lyon on 2018/3/28.
// */
//public class MultiGroupByTest extends TestCase {
//
//    protected List<Column> dimensions;
//    protected Map<RowIndexKey<int[]>, RowTraversal> bitMapGroup;
//    private int rowCount;
//
//    @Override
//    public void setUp() throws Exception {
//        CubeData cubeData = new CubeData();
//        dimensions = new ArrayList<Column>();
//        for (Pair<Column, IndexInfo> pair : cubeData.getDimensions()) {
//            dimensions.add(pair.getKey());
//        }
//        bitMapGroup = cubeData.getBitMapGroup();
//        rowCount = cubeData.getRowCount();
//    }
//
//    public void testNormalRowIndex() {
//        int[] cursor = new int[dimensions.size()];
//        Arrays.fill(cursor, 0);
//        boolean[] asc = new boolean[dimensions.size()];
//        Arrays.fill(asc, true);
//        Iterator<KeyValue<RowIndexKey<int[]>, RowTraversal[]>> iterator = new MultiGroupByIndex(dimensions, new DetailFilter() {
//            @Override
//            public ImmutableBitMap createFilterIndex() {
//                return BitMaps.newAllShowBitMap(rowCount);
//            }
//
//            @Override
//            public boolean matches(SwiftNode node, int targetIndex, MatchConverter converter) {
//                return false;
//            }
//        }, cursor, asc);
//        while (iterator.hasNext()) {
//            KeyValue<RowIndexKey<int[]>, RowTraversal[]> keyValue = iterator.next();
//            assertTrue(bitMapGroup.containsKey(keyValue.getKey()));
//            RowTraversal traversal = keyValue.getValue()[keyValue.getKey().getKey().length];
//            final ImmutableBitMap expectedBitMap = bitMapGroup.get(keyValue.getKey()).toBitMap();
//            assertTrue(traversal.getCardinality() == expectedBitMap.getCardinality());
//            traversal.traversal(new TraversalAction() {
//                @Override
//                public void actionPerformed(int row) {
//                    assertTrue(expectedBitMap.contains(row));
//                }
//            });
//        }
//    }
//}
