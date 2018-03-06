package com.fr.swift.query.group.multiby;

import com.fr.swift.bitmap.BitMaps;
import com.fr.swift.bitmap.ImmutableBitMap;
import com.fr.swift.bitmap.traversal.TraversalAction;
import com.fr.swift.mapreduce.KeyValue;
import com.fr.swift.query.group.by.multiby.GroupByIndexIterable;
import com.fr.swift.result.RowIndexKey;
import com.fr.swift.segment.column.Column;
import com.fr.swift.structure.iterator.RowTraversal;
import junit.framework.TestCase;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by Lyon on 2017/12/28.
 */
public class GroupByIndexIterableTest extends TestCase {
    List<Column> dimensions;
    Map<RowIndexKey, RowTraversal> bitMapGroup;

    @Override
    public void setUp() throws Exception {
        CubeData cubeData = new CubeData();
        dimensions = cubeData.getDimensions();
        bitMapGroup = cubeData.getBitMapGroup();
    }

    public void testSumRowIndex() {
        GroupByIndexIterable indexIterator = new GroupByIndexIterable(dimensions, null);
        indexIterator.setShowSum(true);
        Iterator<KeyValue<RowIndexKey, RowTraversal>> iterator = indexIterator.iterator();
        while (iterator.hasNext()) {
            KeyValue<RowIndexKey, RowTraversal> keyValue = iterator.next();
            if (!keyValue.getKey().isSum()) {
                continue;
            }
            ImmutableBitMap expectedBitMap = getSumBitMap(keyValue.getKey());
            assertEquals(keyValue.getValue().getCardinality(), expectedBitMap.getCardinality());
            keyValue.getValue().traversal(new TraversalAction() {
                @Override
                public void actionPerformed(int row) {
                    assertTrue(expectedBitMap.contains(row));
                }
            });
        }
    }

    private ImmutableBitMap getSumBitMap(RowIndexKey rowIndex) {
        ImmutableBitMap bitMap = BitMaps.newRoaringMutable();
        for (Map.Entry<RowIndexKey, RowTraversal> entry : bitMapGroup.entrySet()) {
            if (isSubIndex(rowIndex, entry.getKey())) {
                bitMap = bitMap.getOr(entry.getValue().toBitMap());
            }
        }
        return bitMap;
    }

    private static boolean isSubIndex(RowIndexKey sumRow, RowIndexKey subRow) {
        int[] sumIndexes = sumRow.getValues();
        int[] subIndexes = subRow.getValues();
        for (int i = 0; i < sumIndexes.length; i++) {
            if (sumIndexes[i] == -1) {
                break;
            }
            if (sumIndexes[i] != subIndexes[i]) {
                return false;
            }
        }
        return true;
    }

    public void testNext() {
        GroupByIndexIterable indexIterator = new GroupByIndexIterable(dimensions, null);
        Iterator<KeyValue<RowIndexKey, RowTraversal>> iterator = indexIterator.iterator();
        while (iterator.hasNext()) {
            KeyValue<RowIndexKey, RowTraversal> keyValue = iterator.next();
            if (!bitMapGroup.containsKey(keyValue.getKey())) {
                System.out.println();
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