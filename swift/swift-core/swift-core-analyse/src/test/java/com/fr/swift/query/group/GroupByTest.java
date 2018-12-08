package com.fr.swift.query.group;

import com.fr.swift.bitmap.ImmutableBitMap;
import com.fr.swift.bitmap.MutableBitMap;
import com.fr.swift.bitmap.impl.BitSetMutableBitMap;
import com.fr.swift.bitmap.traversal.TraversalAction;
import com.fr.swift.cube.io.location.IResourceLocation;
import com.fr.swift.query.group.by.GroupBy;
import com.fr.swift.query.group.by.GroupByEntry;
import com.fr.swift.query.group.by.GroupByResult;
import com.fr.swift.segment.column.BitmapIndexedColumn;
import com.fr.swift.segment.column.Column;
import com.fr.swift.segment.column.DetailColumn;
import com.fr.swift.segment.column.DictionaryEncodedColumn;
import com.fr.swift.test.Temps.TempDictColumn;
import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by pony on 2017/11/27.
 */
public class GroupByTest extends TestCase {

    private Column column;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        column = new Column() {

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
            public boolean isReadable() {
                return false;
            }

            @Override
            public void flush() {

            }

            @Override
            public void putBitMapIndex(int index, ImmutableBitMap bitmap) {

            }

            @Override
            public ImmutableBitMap getBitMapIndex(int index) {
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
        final String[] keys = {"A","B","C"};
        final int[] index = {0,1,2,1,2,1,0,2,1};
        return new TempDictColumn() {

            @Override
            public int size() {
                return 3;
            }

            @Override
            public Object getValue(int index) {
                return keys[index];
            }

            @Override
            public int getIndexByRow(int row) {
                return index[row];
            }
        };
    }

    public void testCreateGroupByResult() {
        MutableBitMap mBitMap = BitSetMutableBitMap.newInstance();
        mBitMap.add(0);
        mBitMap.add(1);
        mBitMap.add(3);
        GroupByResult result = GroupBy.createGroupByResult(column, mBitMap, true);
        List<GroupByEntry> list = new ArrayList<GroupByEntry>();
        while (result.hasNext()){
            list.add(result.next());
        }
        assertEquals(list.size(), 2);
        assertEquals(column.getDictionaryEncodedColumn().getValue(list.get(0).getIndex()), "A");
        assertEquals(column.getDictionaryEncodedColumn().getValue(list.get(1).getIndex()), "B");
        final List<Integer> value0 = new ArrayList<Integer>();
        list.get(0).getTraversal().traversal(new TraversalAction() {
            @Override
            public void actionPerformed(int row) {
                value0.add(row);
            }
        });
        final List<Integer> value1 = new ArrayList<Integer>();
        list.get(1).getTraversal().traversal(new TraversalAction() {
            @Override
            public void actionPerformed(int row) {
                value1.add(row);
            }
        });
        assertEquals(value0.size(), 1);
        assertEquals(value0.get(0).intValue(), 0);
        assertEquals(value1.size(), 2);
        assertEquals(value1.get(0).intValue(), 1);
        assertEquals(value1.get(1).intValue(), 3);
    }

}