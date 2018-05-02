package com.fr.swift.query.group.by;

import com.fr.swift.bitmap.BitMaps;
import com.fr.swift.bitmap.ImmutableBitMap;
import com.fr.swift.bitmap.traversal.TraversalAction;
import com.fr.swift.query.filter.detail.DetailFilter;
import com.fr.swift.result.KeyValue;
import com.fr.swift.result.SwiftNode;
import com.fr.swift.result.row.RowIndexKey;
import com.fr.swift.segment.column.Column;
import com.fr.swift.structure.iterator.RowTraversal;
import junit.framework.TestCase;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by Lyon on 2017/12/28.
 */
public class MultiDimensionGroupByTest extends TestCase {
    protected List<Column> dimensions;
    protected Map<RowIndexKey<int[]>, RowTraversal> bitMapGroup;
    private int rowCount;

    @Override
    public void setUp() throws Exception {
        CubeData cubeData = new CubeData();
        dimensions = cubeData.getDimensions();
        bitMapGroup = cubeData.getBitMapGroup();
        rowCount = cubeData.getRowCount();
    }

    public void testSumRowIndex() {
        int[] cursor = new int[dimensions.size()];
        Arrays.fill(cursor, 0);
        boolean[] asc = new boolean[dimensions.size()];
        Arrays.fill(asc, true);
        Iterator<KeyValue<RowIndexKey<int[]>, RowTraversal>> iterator = new MultiDimensionGroupBy(dimensions, new DetailFilter() {
            @Override
            public ImmutableBitMap createFilterIndex() {
                return BitMaps.newAllShowBitMap(rowCount);
            }

            @Override
            public boolean matches(SwiftNode node, int targetIndex) {
                return false;
            }
        }, cursor, asc);
        while (iterator.hasNext()) {
            KeyValue<RowIndexKey<int[]>, RowTraversal> keyValue = iterator.next();
            int[] key = keyValue.getKey().getKey();
            if (key[key.length - 1] != -1) {
                continue;
            }
            ImmutableBitMap expectedBitMap = getSumBitMap(keyValue.getKey().getKey());
            assertEquals(keyValue.getValue().getCardinality(), expectedBitMap.getCardinality());
            keyValue.getValue().traversal(new TraversalAction() {
                @Override
                public void actionPerformed(int row) {
                    assertTrue(expectedBitMap.contains(row));
                }
            });
        }
    }

    private ImmutableBitMap getSumBitMap(int[] key) {
        ImmutableBitMap bitMap = BitMaps.newRoaringMutable();
        for (Map.Entry<RowIndexKey<int[]>, RowTraversal> entry : bitMapGroup.entrySet()) {
            if (isSubIndex(key, entry.getKey().getKey())) {
                bitMap = bitMap.getOr(entry.getValue().toBitMap());
            }
        }
        return bitMap;
    }

    private static boolean isSubIndex(int[] summaryKey, int[] subIndexes) {
        for (int i = 0; i < summaryKey.length; i++) {
            if (summaryKey[i] == -1) {
                break;
            }
            if (summaryKey[i] != subIndexes[i]) {
                return false;
            }
        }
        return true;
    }

    private static boolean isNormalRow(int[] index) {
        for (int i = 0; i < index.length; i++) {
            if (index[i] == -1) {
                return false;
            }
        }
        return true;
    }

    public void testNormalRowIndex() {
        int[] cursor = new int[dimensions.size()];
        Arrays.fill(cursor, 0);
        boolean[] asc = new boolean[dimensions.size()];
        Arrays.fill(asc, true);
        Iterator<KeyValue<RowIndexKey<int[]>, RowTraversal>> iterator = new MultiDimensionGroupBy(dimensions, new DetailFilter() {
            @Override
            public ImmutableBitMap createFilterIndex() {
                return BitMaps.newAllShowBitMap(rowCount);
            }

            @Override
            public boolean matches(SwiftNode node, int targetIndex) {
                return false;
            }
        }, cursor, asc);
        while (iterator.hasNext()) {
            KeyValue<RowIndexKey<int[]>, RowTraversal> keyValue = iterator.next();
            if (!isNormalRow(keyValue.getKey().getKey())) {
                continue;
            }
            assertTrue(bitMapGroup.containsKey(keyValue.getKey()));
            RowTraversal traversal = keyValue.getValue();
            ImmutableBitMap expectedBitMap = bitMapGroup.get(keyValue.getKey()).toBitMap();
            assertTrue(traversal.getCardinality() == expectedBitMap.getCardinality());
            traversal.traversal(new TraversalAction() {
                @Override
                public void actionPerformed(int row) {
                    assertTrue(expectedBitMap.contains(row));
                }
            });
        }
    }
}