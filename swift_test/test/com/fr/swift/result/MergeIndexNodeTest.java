package com.fr.swift.result;

import com.fr.swift.Temps.TempDictColumn;
import com.fr.swift.segment.column.DictionaryEncodedColumn;
import junit.framework.TestCase;

import java.util.Comparator;

/**
 * Created by pony on 2017/12/8.
 */
public class MergeIndexNodeTest extends TestCase {

    MergeIndexNode node;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        final String[] keys = {"A","B","C"};
        final int[] index = {0,1,2,1,2,1,0,2,1};
        DictionaryEncodedColumn column = new TempDictColumn() {
            @Override
            public void flush() {

            }

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

            @Override
            public void putGlobalIndex(int index, int globalIndex) {

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
            public void release() {

            }

            @Override
            public Comparator getComparator() {
                return null;
            }

            @Override
            public Object convertValue(Object value) {
                return value;
            }
        };
        node = new MergeIndexNode(1, 1, 1, new DictionaryEncodedColumn[]{column, column});
    }



    public void testInitDataByIndex() {
        assertEquals(node.getData(), "B");
    }

    public void testCreateKey() {
        assertEquals(node.createKey(), ((long)1 << 31) + 1);
    }
}