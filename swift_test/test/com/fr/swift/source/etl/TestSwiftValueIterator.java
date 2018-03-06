package com.fr.swift.source.etl;

import com.fr.swift.bitmap.ImmutableBitMap;
import com.fr.swift.bitmap.MutableBitMap;
import com.fr.swift.bitmap.impl.BitSetMutableBitMap;
import com.fr.swift.cube.io.location.IResourceLocation;
import com.fr.swift.relation.CubeLogicColumnKey;
import com.fr.swift.relation.CubeMultiRelation;
import com.fr.swift.relation.CubeMultiRelationPath;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.column.BitmapIndexedColumn;
import com.fr.swift.segment.column.Column;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.segment.column.DetailColumn;
import com.fr.swift.segment.column.DictionaryEncodedColumn;
import com.fr.swift.segment.relation.RelationIndex;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.source.etl.utils.GVIAndSegment;
import com.fr.swift.source.etl.utils.SwiftValueIterator;
import junit.framework.TestCase;

import java.util.Comparator;

/**
 * Created by Handsome on 2017/12/23 0023 14:00
 */
public class TestSwiftValueIterator extends TestCase {
    private Column column1;
    private Column column2;
    private Segment segment1;
    private Segment segment2;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        column1 = new Column() {

            @Override
            public DictionaryEncodedColumn getDictionaryEncodedColumn() {
                return createDicColumn();
            }

            @Override
            public BitmapIndexedColumn getBitmapIndex() {
                return createBitmapColumn();
            }

            @Override
            public DetailColumn getDetailColumn() {
                return null;
            }

            @Override
            public IResourceLocation getLocation() {
                return null;
            }
        };

        column2 = new Column() {


            @Override
            public DictionaryEncodedColumn getDictionaryEncodedColumn() {
                return createDicColumn();
            }

            @Override
            public BitmapIndexedColumn getBitmapIndex() {
                return createBitmapColumn();
            }

            @Override
            public DetailColumn getDetailColumn() {
                return null;
            }

            @Override
            public IResourceLocation getLocation() {
                return null;
            }
        };
        segment1 = new Segment() {

            @Override
            public void flush() {

            }

            @Override
            public int getRowCount() {
                return 9;
            }

            @Override
            public void putRowCount(int rowCount) {

            }

            @Override
            public RelationIndex getRelation(CubeMultiRelation f) {
                return null;
            }

            @Override
            public RelationIndex getRelation(CubeMultiRelationPath f) {
                return null;
            }

            @Override
            public RelationIndex getRelation(CubeLogicColumnKey f) {
                return null;
            }

            @Override
            public IResourceLocation getLocation() {
                return null;
            }

            @Override
            public Column getColumn(ColumnKey key) {
                if (key.getName().equals("column1")) {
                    return column1;
                }
                if (key.getName().equals("column2")) {
                    return column2;
                }
                return null;
            }

            @Override
            public ImmutableBitMap getAllShowIndex() {
                MutableBitMap bitMap = BitSetMutableBitMap.newInstance();
                for (int i = 0; i < getRowCount(); i++) {
                    bitMap.add(i);
                }
                return bitMap;
            }

            @Override
            public void putAllShowIndex(ImmutableBitMap bitMap) {

            }

            @Override
            public SwiftMetaData getMetaData() {
                return null;
            }

            @Override
            public void release() {

            }

            @Override
            public boolean isHistory() {
                return false;
            }
        };
        segment2 = new Segment() {


            @Override
            public void flush() {

            }

            @Override
            public int getRowCount() {
                return 9;
            }

            @Override
            public void putRowCount(int rowCount) {

            }

            @Override
            public RelationIndex getRelation(CubeMultiRelation f) {
                return null;
            }

            @Override
            public RelationIndex getRelation(CubeMultiRelationPath f) {
                return null;
            }

            @Override
            public RelationIndex getRelation(CubeLogicColumnKey f) {
                return null;
            }

            @Override
            public IResourceLocation getLocation() {
                return null;
            }

            @Override
            public Column getColumn(ColumnKey key) {
                if (key.getName().equals("column1")) {
                    return column1;
                }
                if (key.getName().equals("column2")) {
                    return column2;
                }
                return null;
            }

            @Override
            public ImmutableBitMap getAllShowIndex() {
                MutableBitMap bitMap = BitSetMutableBitMap.newInstance();
                for (int i = 0; i < getRowCount(); i++) {
                    bitMap.add(i);
                }
                return bitMap;
            }

            @Override
            public void putAllShowIndex(ImmutableBitMap bitMap) {

            }

            @Override
            public SwiftMetaData getMetaData() {
                return null;
            }

            @Override
            public void release() {

            }

            @Override
            public boolean isHistory() {
                return false;
            }
        };
    }

    private BitmapIndexedColumn createBitmapColumn() {
        final MutableBitMap[] bitMaps = new MutableBitMap[3];
        bitMaps[0] = BitSetMutableBitMap.newInstance();
        bitMaps[1] = BitSetMutableBitMap.newInstance();
        bitMaps[2] = BitSetMutableBitMap.newInstance();
        bitMaps[0].add(0);
        bitMaps[0].add(6);
        bitMaps[1].add(1);
        bitMaps[1].add(3);
        bitMaps[1].add(5);
        bitMaps[1].add(8);
        bitMaps[2].add(2);
        bitMaps[2].add(4);
        bitMaps[2].add(7);
        return new BitmapIndexedColumn() {
            @Override
            public void flush() {

            }

            @Override
            public void putBitMapIndex(int index, ImmutableBitMap bitmap) {

            }

            @Override
            public ImmutableBitMap getBitMapIndex(int index) {
                if (index < bitMaps.length) {
                    return bitMaps[index];
                }
                return null;
            }

            @Override
            public void putNullIndex(ImmutableBitMap bitMap) {

            }

            @Override
            public ImmutableBitMap getNullIndex() {
                return null;
            }

            @Override
            public void release() {

            }
        };

    }

    private DictionaryEncodedColumn createDicColumn() {
        final String[] keys = {"A", "B", "C"};
        final int[] index = {0, 1, 2, 1, 2, 1, 0, 2, 1};
        return new DictionaryEncodedColumn() {

            @Override
            public void flush() {

            }

            @Override
            public int getGlobalIndexByRow(int row) {
                return 0;
            }

            @Override
            public int getGlobalIndexByIndex(int index) {
                return 0;
            }

            @Override
            public int size() {
                return 3;
            }

            @Override
            public void putGlobalSize(int globalSize) {

            }

            @Override
            public int globalSize() {
                return 0;
            }

            @Override
            public void putSize(int size) {

            }

            @Override
            public Object getValue(int index) {
                return keys[index];
            }

            @Override
            public void putValue(int index, Object val) {

            }

            @Override
            public int getIndex(Object value) {
                return 0;
            }

            @Override
            public void putIndex(int row, int index) {

            }

            @Override
            public int getIndexByRow(int row) {
                return index[row];
            }

            @Override
            public void putGlobalIndex(int index, int globalIndex) {

            }

            @Override
            public void release() {

            }

            @Override
            public Comparator getComparator() {
                return new Comparator() {

                    @Override
                    public int compare(Object o3, Object o4) {
                        String o1 = (String) o3;
                        String o2 = (String) o4;
                        return o1.compareTo(o2);
                    }
                };
            }
        };
    }


    public void testValueIterator() {
        ColumnKey key1 = new ColumnKey("column1");
        ColumnKey key2 = new ColumnKey("column2");
        //RowTraversal mock = EasyMock.createMock(RowTraversal.class);
        //EasyMock.expect(mock.toBitMap()).andReturn();
        Segment[] segments = new Segment[2];
        segments[0] = segment1;
        segments[1] = segment2;
        ColumnKey[] keys = new ColumnKey[2];
        keys[0] = key1;
        keys[1] = key2;
        SwiftValueIterator valueIterator = new SwiftValueIterator(segments, keys);
        String[][] str = new String[][]{{"A", "A"}, {"A", "A"}, {"A", "A"}, {"A", "A"}, {"B", "B"}, {"B", "B"}, {"B", "B"}, {"B", "B"},
                {"B", "B"}, {"B", "B"}, {"B", "B"}, {"B", "B"}, {"C", "C"}, {"C", "C"}, {"C", "C"}, {"C", "C"}, {"C", "C"}, {"C", "C"}};
        int index = 0;
        while (valueIterator.hasNext()) {
            GVIAndSegment gviAndSegment = valueIterator.next();
            for (int i = 0; i < gviAndSegment.getValues().length; i++) {
                assertEquals(gviAndSegment.getValues()[i], str[index][i]);
            }
            index++;
        }
    }
}
